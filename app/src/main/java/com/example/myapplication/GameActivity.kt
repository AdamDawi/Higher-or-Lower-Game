package com.example.myapplication

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityGameBinding
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
    private var customProgressDialog: Dialog? = null
    //stores countries that have already been used
    private lateinit var usedCountriesIds: HashSet<Int>
    //storing the countries that are currently displayed
    private var countryUp: CountryModel? = null
    private var countryDown: CountryModel? = null
    private var score: Int = 0
    //storing animations that can be launched later
    private lateinit var animatorUp: ValueAnimator
    private lateinit var animatorDown: ValueAnimator
    //countryThatWon = -1 because we want to start animations for countryUp and countryDown at the start of the activity
    private var countryThatWon: Int = -1
    private val animationDelay: Long = 1000
    //storing data on the phone
    private lateinit var mSharedPreferences: SharedPreferences
    //if we need load data is set to 0 if not is !=0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProgressDialog()
        usedCountriesIds = HashSet()

        //shared preferences only visible to this app
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        //if 0 you need load data from api if not you don't need load
        val loadData = intent.getIntExtra("loadData", 0)

        //we don't need load data from api again because we have it in files
        if(loadData!=0 && Constants.isNetworkAvailable(this))
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
        val service:CountryService = retrofit.create(CountryService::class.java)
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
                    editor.putString(Constants.WEATHER_RESPONSE_DATA, countryListResponseJsonString)
                    editor.apply()

                    //changing graphics elements must be in main Thread
                    runOnUiThread {
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
        //getting data from phone
        val countryListResponseJsonString = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")

        if(!countryListResponseJsonString.isNullOrEmpty() && countryList.isNullOrEmpty())
        {
            //get countries data from phone
            countryList = Gson().fromJson<List<CountryModel>>(
                countryListResponseJsonString,
                object : TypeToken<List<CountryModel>>() {}.type
            )

            //changing graphics elements must be in main Thread
            runOnUiThread {
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
            //TODO end dialog
            Toast.makeText(this@GameActivity, "You ended game. No more countries to display.", Toast.LENGTH_LONG).show()
            finish()
        }
        //add id to used ids
        usedCountriesIds.add(randomNumber)
        return randomNumber
    }

    private fun checkCountriesPopulation(countryType: Int)
    {
        //blocking clicks on Images
        binding?.ivImageUp?.isClickable = false
        binding?.ivImageDown?.isClickable = false
        binding?.ivImageUp?.isFocusable = false
        binding?.ivImageDown?.isFocusable = false

        changeGraphicsElementsVisibility()
        //checking which country is about to changed population number
        if(countryThatWon==-1)
        {
            animatorDown.start()
            animatorUp.start()
        }
        else
        {
            animatorDown.start()
        }

        CoroutineScope(Dispatchers.Main).launch {
            val countryUpPopulation = Integer.parseInt(countryUp!!.population)
            val countryDownPopulation = Integer.parseInt(countryDown!!.population)
            delay(animationDelay)

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
            //after marking the results and animating tv population
            delay(animationDelay)

            if(countryType==Constants.UP_COUNTRY && countryUpPopulation>=countryDownPopulation)
            {
                score++
                countryThatWon=Constants.UP_COUNTRY
                binding?.tvScoreNumber?.text = score.toString()

            }
            else if(countryType==Constants.DOWN_COUNTRY && countryUpPopulation<=countryDownPopulation)
            {
                score++
                countryThatWon=Constants.DOWN_COUNTRY
                binding?.tvScoreNumber?.text = score.toString()
            }
            else
            {
                //TODO make end dialog
                Toast.makeText(this@GameActivity, "You lose", Toast.LENGTH_SHORT).show()

                //End activity with return
                finish()
                return@launch
            }

            //setting next country
            //first new country is added to duplicated country elements
            val countryId = getRandomCountryId()
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(countryList?.get(countryId)?.flags?.png).into(binding!!.ivImageDuplicate)
            binding?.tvNameDuplicate?.text = countryList?.get(countryId)?.name?.common

            binding?.tvNameDuplicate?.visibility = View.VISIBLE
            binding?.ivImageDuplicate?.visibility = View.VISIBLE

            binding?.flOR?.alpha=0.3f
            SlideAnim.onStartAnimation(binding?.llUp!!, binding?.llDown!!, binding?.llDuplicate!!)

            delay(800)
            setCountryData(countryDown!!, Constants.UP_COUNTRY)
            animatorUp.duration = 0L
            animatorUp.start()

            setCountryData(countryList!![countryId], Constants.DOWN_COUNTRY)

            SlideAnim.reset(binding?.llUp!!, binding?.llDown!!, binding?.llDuplicate!!)

            binding?.flOR?.alpha=1f

            binding?.tvPopulationNumberDown?.visibility = View.GONE
            binding?.tvPopulationDown?.visibility = View.GONE


            binding?.tvNameDuplicate?.visibility = View.INVISIBLE
            binding?.ivImageDuplicate?.visibility = View.INVISIBLE

            binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.blue))
            binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.blue))

            binding?.ivImageUp?.isClickable = true
            binding?.ivImageDown?.isClickable = true
            binding?.ivImageUp?.isFocusable = true
            binding?.ivImageDown?.isFocusable = true
        }

    }

    private fun animateNumberTextView(finalValue: Int, textview: TextView): ValueAnimator{
        val valueAnimator = ValueAnimator.ofInt(0, finalValue)
        //setting delay for animation
        valueAnimator.duration = animationDelay

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

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}