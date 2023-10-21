package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        val listCall: Call<List<ReceivedDataModel>> = service.getCountry()

        listCall.enqueue(object: Callback<List<ReceivedDataModel>>{
            override fun onResponse(
                call: Call<List<ReceivedDataModel>>,
                response: Response<List<ReceivedDataModel>>
            ) {
                if(response.isSuccessful)
                {
                    val countryList: List<ReceivedDataModel>? = response.body()
                    Log.i("Response RESULT: ", "$countryList")
                }
                else
                {
                    val rc = response.code()
                    when(rc)
                    {
                        400 -> Log.e("ERROR 400", "Bad connection")
                        404 -> Log.e("ERROR 404", "Not found")
                        else -> Log.e("ERROR", "Generic error")
                    }
                }
            }

            override fun onFailure(call: Call<List<ReceivedDataModel>>, t: Throwable) {
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