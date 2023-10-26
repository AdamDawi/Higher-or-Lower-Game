package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        var isDataLoaded = -1 //a variable to check if we need to load country data from the API

        binding?.flStart?.setOnClickListener {
            isDataLoaded++;
            val intent = Intent(this, GameActivity::class.java)
            //send variable to game activity
            intent.putExtra("loadData", isDataLoaded)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}