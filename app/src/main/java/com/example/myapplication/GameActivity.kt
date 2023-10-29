package com.example.myapplication

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityGameBinding
import com.example.myapplication.databinding.CustomEndGameDialogBinding
import com.example.myapplication.restAPI.CountryModel
import com.example.myapplication.restAPI.CountryService
import com.example.myapplication.roomDatabase.PlayerApp
import com.example.myapplication.roomDatabase.PlayerDao
import com.example.myapplication.roomDatabase.PlayerEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

import kotlin.random.Random


class GameActivity : AppCompatActivity()
{
    private var binding: ActivityGameBinding? = null
    //storing countries from rest api
    private var countryList: List<CountryModel>? = null
    //custom dialogs
    private var customProgressDialog: Dialog? = null
    private var customEndDialog: Dialog? = null
    //stores countries that have already been used
    private lateinit var usedCountriesIds: HashSet<Int>
    //stores the currently displayed countries
    private var countryUp: CountryModel? = null
    private var countryDown: CountryModel? = null
    //player's score
    private var score: Int = 0
    //storing animations that can be launched later during the turn
    private lateinit var animatorUp: ValueAnimator
    private lateinit var animatorDown: ValueAnimator
    //we want to start animations for countryUp and countryDown at the start of the activity
    private var isBothCountriesAnim = true
    //storing data on the phone
    private lateinit var mSharedPreferences: SharedPreferences
    //if we need country data from the API, we set it to 0, if not, we don't need it
    private var isDataLoaded: Int = 0
    private var playerName: String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //loading dialog
        showProgressDialog()

        usedCountriesIds = HashSet()

        //getting shared preferences only visible to this app
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)

        //getting value from main activity
        isDataLoaded = intent.getIntExtra("loadData", 0)
        playerName = intent.getStringExtra("name") as String

        //we don't need load data from api again because we have it in file
        if(isDataLoaded!=0)
        {
            setupUI()
        }
        //we need to load data from api
        if(countryList.isNullOrEmpty())
        {
            getCountriesDataDetails()
        }

        binding?.ivImageUp?.setOnClickListener {
            checkCountriesPopulation(Constants.UP_COUNTRY)
        }

        binding?.ivImageDown?.setOnClickListener {
            checkCountriesPopulation(Constants.DOWN_COUNTRY)
        }
    }
    private fun getCountriesDataDetails()
    {
        //checking internet connection
        if(!Constants.isNetworkAvailable(this))
        {
            Toast.makeText(this@GameActivity, "You don't have internet connection.", Toast.LENGTH_SHORT).show()
        }
        //making retrofit for fast connection with HTTP
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val service: CountryService = retrofit.create(CountryService::class.java)
        val listCall: Call<List<CountryModel>> = service.getCountry()

        //invoking a request asynchronously
        listCall.enqueue(object: Callback<List<CountryModel>>{
            override fun onResponse(
                call: Call<List<CountryModel>>,
                response: Response<List<CountryModel>>
            ) {
                if(response.isSuccessful)
                {
                    //obtained data
                    countryList = response.body()

                    //converting json to string because we want to store it in shared preferences
                    val countryListResponseJsonString = Gson().toJson(countryList)
                    //start editing
                    val editor = mSharedPreferences.edit()
                    editor.putString(Constants.COUNTRY_RESPONSE_DATA, countryListResponseJsonString)
                    editor.apply()

                    //changing graphics elements must be in main Thread
                    runOnUiThread {
                        //changing the visibility of elements to visible because they are invisible at the start
                        binding?.flOR?.visibility = View.VISIBLE
                        binding?.tvScoreNumber?.visibility = View.VISIBLE
                        binding?.tvScore?.visibility = View.VISIBLE

                        //setting the first countries data from the API
                        setCountryData(countryList!![getRandomCountryId()], Constants.UP_COUNTRY)
                        setCountryData(countryList!![getRandomCountryId()], Constants.DOWN_COUNTRY)

                        cancelProgressDialog()
                    }
                }
                else
                {
                    when(response.code())
                    {
                        400 -> Log.e("ERROR 400", "Bad connection")
                        404 -> Log.e("ERROR 404", "Not found")
                        else -> Log.e("ERROR", "Generic error")
                    }
                }
            }
            override fun onFailure(call: Call<List<CountryModel>>, t: Throwable) {
                Log.e("ERROR", t.message.toString())
            }
        })
    }
    private fun setupUI()
    {
        //getting data from phone's file
        val countryListResponseJsonString = mSharedPreferences.getString(Constants.COUNTRY_RESPONSE_DATA, "")

        if(!countryListResponseJsonString.isNullOrEmpty() && countryList.isNullOrEmpty())
        {
            //get countries data from phone
            countryList = Gson().fromJson<List<CountryModel>>(
                countryListResponseJsonString,
                object : TypeToken<List<CountryModel>>() {}.type
            )

            //changing the visibility of elements to visible because they are invisible at the start
            binding?.flOR?.visibility = View.VISIBLE
            binding?.tvScoreNumber?.visibility = View.VISIBLE
            binding?.tvScore?.visibility = View.VISIBLE

            //setting the first country data from the API
            setCountryData(countryList!![getRandomCountryId()], Constants.UP_COUNTRY)
            setCountryData(countryList!![getRandomCountryId()], Constants.DOWN_COUNTRY)

            cancelProgressDialog()
        }
    }

    private fun setCountryData(country: CountryModel, countryType: Int)
    {
        if(countryType==Constants.UP_COUNTRY)
        {
            countryUp = country
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivImageUp)
            binding?.tvNameUp?.text = country.name.common
            //preparing animate for population number
            animatorUp = animateNumberTextView(Integer.parseInt(country.population), binding?.tvPopulationNumberUp!!)
        }else if(countryType==Constants.DOWN_COUNTRY)
        {
            countryDown = country
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivImageDown)
            binding?.tvNameDown?.text = country.name.common
            //preparing animate for population number
            animatorDown = animateNumberTextView(Integer.parseInt(country.population), binding?.tvPopulationNumberDown!!)
        }
    }

    private fun showProgressDialog()
    {
        customProgressDialog = Dialog(this@GameActivity)
        customProgressDialog?.setContentView(R.layout.custom_progress_dialog)
        customProgressDialog?.setCancelable(false)
        customProgressDialog?.show()
    }

    private fun showEndDialog()
    {
        customEndDialog = Dialog(this@GameActivity)
        val dialogBinding = CustomEndGameDialogBinding.inflate(layoutInflater)
        customEndDialog?.setContentView(dialogBinding.root)

        //setting text on end dialog with result score
        if(score == 1) dialogBinding.tvResultScore.text = "You scored $score point"
        else dialogBinding.tvResultScore.text = "You scored $score points"

        val playerDao = (application as PlayerApp).database.playerDao()
        addRecordToDatabase(playerDao)

        dialogBinding.btnTryAgain.setOnClickListener {

            //start game activity again
            isDataLoaded++

            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("loadData", isDataLoaded)

            startActivity(intent)
            //fade animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            //make delay
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                finish()
            }, 2000)

        }

        dialogBinding.btnExit.setOnClickListener {
            customEndDialog?.dismiss()

            finish()
            //fade animation for finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        customEndDialog?.setCancelable(false)
        customEndDialog?.show()
    }

    private fun cancelProgressDialog()
    {
        customProgressDialog?.dismiss()
    }

    private fun changeGraphicsElementsVisibility()
    {
        //change elements visibility to visible
        binding?.tvPopulationDown?.visibility = View.VISIBLE
        binding?.tvPopulationNumberDown?.visibility = View.VISIBLE
        binding?.tvPopulationUp?.visibility = View.VISIBLE
        binding?.tvPopulationNumberUp?.visibility = View.VISIBLE
    }

    private fun getRandomCountryId(): Int
    {
        // Create class object
        val random = Random(System.currentTimeMillis())
        // Generate random number from 0 to borderNumber which is number of countries
        var randomNumber = random.nextInt(0, countryList?.size ?:0)

        //checks whether the id has not already appeared before
        while(usedCountriesIds.contains(randomNumber) && usedCountriesIds.size!=countryList?.size)
        {
            randomNumber = random.nextInt(0, countryList?.size?:0)
        }

        if(usedCountriesIds.size==countryList?.size)
        {
            showEndDialog()
            Toast.makeText(this@GameActivity, "You ended game. No more countries to display.", Toast.LENGTH_LONG).show()
        }
        //add id to used ids
        usedCountriesIds.add(randomNumber)
        return randomNumber
    }

    private fun checkCountriesPopulation(countryType: Int)
    {
        //blocking clicks on images
        binding?.ivImageUp?.isClickable = false
        binding?.ivImageDown?.isClickable = false
        binding?.ivImageUp?.isFocusable = false
        binding?.ivImageDown?.isFocusable = false

        changeGraphicsElementsVisibility()
        //checking which country is about to changed population number
        if(isBothCountriesAnim)
        {
            animatorDown.start()
            animatorUp.start()
        }
        else
        {
            animatorDown.start()
        }

        CoroutineScope(Dispatchers.Main).launch {
            //setting the population number
            val countryUpPopulation = Integer.parseInt(countryUp!!.population)
            val countryDownPopulation = Integer.parseInt(countryDown!!.population)

            delay(Constants.NUMBER_ANIM_DELAY)
            //mark lower and higher population
            if(countryUpPopulation>=countryDownPopulation)
            {
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.darkGreen))
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.darkRed))
            }
            else
            {
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.darkRed))
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.darkGreen))
            }
            //after marking the results and animating population text view
            delay(Constants.NUMBER_ANIM_DELAY)
            var isDialogVisible = false

            //check if lose
            if((countryType==Constants.UP_COUNTRY && countryUpPopulation>=countryDownPopulation)
                || (countryType==Constants.DOWN_COUNTRY && countryUpPopulation<=countryDownPopulation))
            {
                score++
                isBothCountriesAnim=false
                binding?.tvScoreNumber?.text = score.toString()
            }
            else
            {
                showEndDialog()
                isDialogVisible=true
            }
            if(!isDialogVisible)
            {
                nextTurn()
            }
        }

    }

    //next turn animation and get next country to choose from
    private suspend fun nextTurn()
    {
        //setting next country
        //first new country is added to duplicated country elements only for animation purpose
        val countryId = getRandomCountryId()
        //Glide for load images from link to xml element
        Glide.with(this@GameActivity).load(countryList?.get(countryId)?.flags?.png).into(binding!!.ivImageDuplicate)
        binding?.tvNameDuplicate?.text = countryList?.get(countryId)?.name?.common

        binding?.tvNameDuplicate?.visibility = View.VISIBLE
        binding?.ivImageDuplicate?.visibility = View.VISIBLE

        binding?.flOR?.alpha=0.3f
        //animation with 3 displayed countries when one is fake only for animation
        SlideAnimation.onStartAnimation(binding?.llUp!!, binding?.llDown!!, binding?.llDuplicate!!)

        //waiting for animation to end
        delay(Constants.NEXT_TURN_ANIM_DELAY)

        //setting upper country to bottom country
        setCountryData(countryDown!!, Constants.UP_COUNTRY)

        //this is only for number format with ','
        animatorUp.duration = 0L
        animatorUp.start()

        //setting bottom country to new country
        setCountryData(countryList!![countryId], Constants.DOWN_COUNTRY)

        //resetting position to starting
        SlideAnimation.reset(binding?.llUp!!, binding?.llDown!!, binding?.llDuplicate!!)

        binding?.flOR?.alpha=1f

        //population in bottom country disappears because we want to not give player information about that
        binding?.tvPopulationNumberDown?.visibility = View.GONE
        binding?.tvPopulationDown?.visibility = View.GONE

        //the supporting country disappears
        binding?.tvNameDuplicate?.visibility = View.INVISIBLE
        binding?.ivImageDuplicate?.visibility = View.INVISIBLE

        //reset colors for text in countries
        binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.blue))
        binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.blue))

        //unblocking clicks on images
        binding?.ivImageUp?.isClickable = true
        binding?.ivImageDown?.isClickable = true
        binding?.ivImageUp?.isFocusable = true
        binding?.ivImageDown?.isFocusable = true
    }

    private fun animateNumberTextView(finalValue: Int, textview: TextView): ValueAnimator{
        val valueAnimator = ValueAnimator.ofInt(0, finalValue)
        //setting delay for animation
        valueAnimator.duration = Constants.NUMBER_ANIM_DELAY

        //decimal format for separator between numbers
        val decimalFormat = DecimalFormat("###,###")
        //setting ',' as thousand separator for decimal format
        decimalFormat.decimalFormatSymbols = DecimalFormatSymbols().apply {
            groupingSeparator = ','
        }

        valueAnimator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            val formattedValue = decimalFormat.format(animatedValue)
            textview.text = formattedValue
        }
        return valueAnimator
    }

    private fun addRecordToDatabase(playerDao: PlayerDao) {
        val name = playerName
        val points = score
        if (points!=0) {
            //coroutine
            lifecycleScope.launch {
                //skip id because it is autoincrement
                playerDao.insert(PlayerEntity(name = name, points = points))
                Toast.makeText(applicationContext, "Result saved", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "U don't have enough points to leaderboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}