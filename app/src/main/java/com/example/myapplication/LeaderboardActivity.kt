package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityLeaderboardBinding
import com.example.myapplication.roomDatabase.*
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {
    private var binding: ActivityLeaderboardBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val playerDao = (application as PlayerApp).database.playerDao()

        //load data from background
        lifecycleScope.launch {
            //getting sorted player list from database to leaderboard
            playerDao.getPlayersSortedByPointsDescending().collect {
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list)
            }
        }
    }

    private fun setupListOfDataIntoRecyclerView(playerList: ArrayList<PlayerEntity>) {
        if (playerList.isNotEmpty()) {
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(playerList)
            // adapter instance is set to the recyclerview to inflate the items.
            binding?.rvLeaderboard?.adapter = itemAdapter
            // Set the LayoutManager that this RecyclerView will use.
            val layoutManager = LinearLayoutManager(this)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            binding?.rvLeaderboard?.layoutManager = layoutManager
            binding?.rvLeaderboard?.visibility = View.VISIBLE
        } else {
            binding?.rvLeaderboard?.visibility = View.GONE
            binding?.tvNoplayers?.visibility = View.VISIBLE
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}
