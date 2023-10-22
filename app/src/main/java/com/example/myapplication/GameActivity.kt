package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityGameBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameActivity : AppCompatActivity()
{
    private var binding: ActivityGameBinding? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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
                    val countryList: List<CountryModel>? = response.body()
                    Log.i("Response RESULT: ", "$countryList")
                    Log.i("Response SIZE: ", countryList?.size.toString())

                    //Glide for load images from link to xml element
                    Glide.with(this@GameActivity).load(countryList!![60].flags.png).into(binding!!.ivCircleImageUp)
                    Glide.with(this@GameActivity).load(countryList[40].flags.png).into(binding!!.ivCircleImageDown)
                    binding?.tvNameUp?.text = countryList!![60].name.common
                    binding?.tvNameDown?.text = countryList!![40].name.common
                    //.override(300, 300).apply(RequestOptions().transform(CircleCrop()))
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




    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}