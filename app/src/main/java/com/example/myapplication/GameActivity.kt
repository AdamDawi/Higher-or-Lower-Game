package com.example.myapplication

import android.animation.ValueAnimator
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityGameBinding
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
    private val animationDelay: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProgressDialog()
        usedCountriesIds = HashSet()
        getCountriesDataDetails()

        binding?.ivImageUp?.setOnClickListener {
            checkCountriesPopulation(Constants.UP_COUNTRY)
        }

        binding?.ivImageDown?.setOnClickListener {
            checkCountriesPopulation(Constants.DOWN_COUNTRY)
        }
    }
    private fun getCountriesDataDetails()
    {
        if(!Constants.isNetworkAvailable(this))
        {
            Toast.makeText(this@GameActivity, "Please turn on internet connection", Toast.LENGTH_LONG).show()
        }
        //making retrofit for fast connection
        val retrofit: Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val service:CountryService = retrofit.create(CountryService::class.java)
        val listCall: Call<List<CountryModel>> = service.getCountry()

        listCall.enqueue(object: Callback<List<CountryModel>>{
            override fun onResponse(
                call: Call<List<CountryModel>>,
                response: Response<List<CountryModel>>
            ) {
                if(response.isSuccessful)
                {
                    //obtained data
                    countryList = response.body()

                    //changing graphics elements must be in main Thread
                    runOnUiThread {
                        //changing the visibility of elements to visible because they are invisible at the start
                        binding?.flOR?.visibility = View.VISIBLE
                        binding?.tvScoreNumber?.visibility = View.VISIBLE
                        binding?.tvScore?.visibility = View.VISIBLE

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

    private fun setCountryData(country: CountryModel, countryType: Int)
    {

        if(countryType==Constants.UP_COUNTRY)
        {
            countryUp = country
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivImageUp)
            binding?.tvNameUp?.text = country.name.common
            animatorUp = animateNumberTextView(Integer.parseInt(country.population), binding?.tvPopulationNumberUp!!)
        }else if(countryType==Constants.DOWN_COUNTRY)
        {
            countryDown = country
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivImageDown)
            binding?.tvNameDown?.text = country.name.common
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
        // Generate random number from 0 to borderNumber
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
            delay(1000)

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

            nextTurnAnim()

            delay(1000)
            setCountryData(countryDown!!, Constants.UP_COUNTRY)
            animatorUp.duration = 0L
            animatorUp.start()

            val views = listOf(binding?.tvPopulationNumberDown, binding?.ivImageDown,  binding?.tvPopulationDown, binding?.tvNameDown)
            for(view in views)
            {
                view?.slideFromOut(1000)
            }

            binding?.tvPopulationNumberDown?.visibility = View.GONE
            binding?.tvPopulationDown?.visibility = View.GONE
            setCountryData(countryList!![getRandomCountryId()], Constants.DOWN_COUNTRY)
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

    private fun nextTurnAnim()
    {
        val views = listOf(binding?.tvPopulationNumberDown, binding?.ivImageDown,  binding?.tvPopulationDown, binding?.tvNameDown)
        val targetViews = listOf(binding?.tvPopulationNumberUp,binding?.ivImageUp,binding?.tvPopulationUp, binding?.tvNameUp)
        animateAllViews(views,targetViews, 1000)

        for(view in targetViews)
        {
            view?.slideTo(view.x, -2000f, 1000)
        }
    }
    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}