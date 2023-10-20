package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity()
{
    private var binding: ActivityGameBinding? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}