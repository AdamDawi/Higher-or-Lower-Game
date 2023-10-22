package com.example.myapplication

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import kotlin.random.Random

class GameActivity : AppCompatActivity()
{
    private var binding: ActivityGameBinding? = null
    private var countryList: List<CountryModel>? = null
    private var customProgressDialog: Dialog? = null
    private var usedCountriesIds: HashSet<Int>? = null
    private var countryUp: CountryModel? = null
    private var countryDown: CountryModel? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProgressDialog()

        getCountriesDataDetails()

        binding?.ivCircleImageUp?.setOnClickListener {
            if(Integer.parseInt(countryUp!!.population)>=Integer.parseInt(countryDown!!.population))
            {
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this, R.color.darkGreen))
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this, R.color.darkRed))
            }
            else
            {
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this, R.color.darkRed))
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this, R.color.darkGreen))
            }
            changeGraphicsElementsVisibility()
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                if(Integer.parseInt(countryUp!!.population)>=Integer.parseInt(countryDown!!.population))
                {
                    binding?.tvPopulationNumberDown?.visibility = View.GONE
                    binding?.tvPopulationDown?.visibility = View.GONE
                    setCountryData(countryList!![getRandomCountryId()], 1)
                }
                else
                {
                    //TODO make end dialog
                    finish()
                }
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.white))
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.white))
            }

        }

        binding?.ivCircleImageDown?.setOnClickListener {
            if(Integer.parseInt(countryUp!!.population)<=Integer.parseInt(countryDown!!.population))
            {
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this, R.color.darkGreen))
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this, R.color.darkRed))
            }
            else
            {
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this, R.color.darkRed))
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this, R.color.darkGreen))
            }
            changeGraphicsElementsVisibility()

            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                if(Integer.parseInt(countryUp!!.population)<=Integer.parseInt(countryDown!!.population))
                {
                    binding?.tvPopulationNumberUp?.visibility = View.GONE
                    binding?.tvPopulationUp?.visibility = View.GONE
                    setCountryData(countryList!![getRandomCountryId()], 0)
                }
                else
                {
                    //TODO make end dialog
                    finish()
                }
                binding?.tvPopulationNumberDown?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.white))
                binding?.tvPopulationNumberUp?.setTextColor(ContextCompat.getColor(this@GameActivity, R.color.white))
            }

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
                    cancelProgressDialog()
                    //changing the visibility of element to visible because they are invisible at the start
                    binding?.flOR?.visibility = View.VISIBLE
                    //obtained data
                    countryList = response.body()

                    setCountryData(countryList!![getRandomCountryId()], 0)
                    setCountryData(countryList!![getRandomCountryId()], 1)
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

    private fun setCountryData(country: CountryModel, countryDataType: Int)
    {
        /*
        0 is upper country on layout
        1 is bottom country on layout
        */
        if(countryDataType==0)
        {
            countryUp = country
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivCircleImageUp)
            binding?.tvNameUp?.text = country.name.common
            binding?.tvPopulationNumberUp?.text = addCommasToPopulationData(country.population)
        }else if(countryDataType==1)
        {
            countryDown = country
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivCircleImageDown)
            binding?.tvNameDown?.text = country.name.common
            binding?.tvPopulationNumberDown?.text = addCommasToPopulationData(country.population)
        }
    }

    private fun addCommasToPopulationData(population: String): String
    {
        val length = population.length
        val stringBuilder = StringBuilder()

        for (i in 0 until length)
        {
            stringBuilder.append(population[i])
            if (i < length - 1 && (i + 1) % 3 == length % 3)
            {
                stringBuilder.append(',')
            }
        }
        return stringBuilder.toString()
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
        var randomNumber = random.nextInt(0, countryList?.size!!)

        //checks whether the id has not already appeared before
        while(usedCountriesIds?.contains(randomNumber) == true)
        {
            randomNumber = random.nextInt(0, countryList?.size!!)
        }
        return randomNumber
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}