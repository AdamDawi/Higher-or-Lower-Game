package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
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

        binding?.btnStart?.setOnClickListener {
            //send variable to game activity
            isDataLoaded++;
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("loadData", isDataLoaded)
            startActivity(intent)
            //fade animation
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding?.btnLeaderboard?.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}