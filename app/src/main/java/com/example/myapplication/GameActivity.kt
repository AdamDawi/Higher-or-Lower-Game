package com.example.myapplication

import android.app.Dialog
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityGameBinding
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
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        showProgressDialog()

        getCountriesDataDetails()
    }
    private fun getCountriesDataDetails()
    {
        if(Constants.isNetworkAvailable(this))
        {
            Toast.makeText(this@GameActivity, "You have internet connection", Toast.LENGTH_SHORT).show()
        }else
        {
            Toast.makeText(this@GameActivity, "You not have internet connection", Toast.LENGTH_SHORT).show()
        }
        //making retrofit connection
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
                    changeGraphicsElementsVisibility()
                    countryList = response.body()
                    Log.i("Response RESULT: ", "$countryList")
                    Log.i("Response SIZE: ", countryList?.size.toString())

                    setCountryData(countryList?.get(getRandomNumber(countryList?.size))!!, 0)
                    setCountryData(countryList!![getRandomNumber(countryList?.size)], 1)
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
        //0 is upper country
        //1 is bottom country
        if(countryDataType==0)
        {
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivCircleImageUp)
            binding?.tvNameUp?.text = country.name.common
            binding?.tvPopulationNumberUp?.text = addCommasToPopulationData(country.population)
        }else if(countryDataType==1)
        {
            //Glide for load images from link to xml element
            Glide.with(this@GameActivity).load(country.flags.png).into(binding!!.ivCircleImageDown)
            binding?.tvNameDown?.text = country.name.common
            Log.e("Test", country.population)
            binding?.tvPopulationNumberDown?.text = addCommasToPopulationData(country.population)
        }

        //.override(300, 300).apply(RequestOptions().transform(CircleCrop()))
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
        customProgressDialog?.setCancelable(true)
        customProgressDialog?.show()
    }

    private fun cancelProgressDialog()
    {
        customProgressDialog?.dismiss()
    }

    private fun changeGraphicsElementsVisibility()
    {
        //changing the visibility of elements because they are invisible at the start
        binding?.tvPopulationUp?.visibility = View.VISIBLE
        binding?.tvPopulationDown?.visibility = View.VISIBLE
        binding?.flOR?.visibility = View.VISIBLE
    }

    private fun getRandomNumber(borderNumber: Int?): Int
    {
        // Create class object
        val random = Random(System.currentTimeMillis())
        // Generate random number from 0 to borderNumber
        return random.nextInt(0, borderNumber!!)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}