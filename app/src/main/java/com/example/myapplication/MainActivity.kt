package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
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
            val name = binding?.etName?.text.toString()
            if(name.isNotBlank())
            {
                //increment because we need to check if this is first time in main activity or not
                isDataLoaded++;
                val intent = Intent(this, GameActivity::class.java)
                //send variables to game activity
                intent.putExtra("loadData", isDataLoaded)
                intent.putExtra("name", name)
                startActivity(intent)
                //fade animation
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                binding?.etName?.clearFocus()
            }
            else{
                binding?.etName?.error = "Invalid name"
                Toast.makeText(this, "Invalid name", Toast.LENGTH_SHORT).show()
            }

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