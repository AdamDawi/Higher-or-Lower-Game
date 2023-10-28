package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

        val playerDao = (application as PlayerApp).db.playerDao()

        lifecycleScope.launch {
            playerDao.fetchAllPlayers().collect {
                Log.d("exactplayer", "$it")
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, playerDao)
            }
        }

    }

    private fun setupListOfDataIntoRecyclerView(playerList: ArrayList<PlayerEntity>, playerDao: PlayerDao) {
        if (playerList.isNotEmpty()) {
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(playerList,{updateId ->
                updateRecordDialog(updateId,playerDao)
            }){ deleteId->
                lifecycleScope.launch {
                    playerDao.fetchPlayerById(deleteId).collect {
                        if (it != null) {
                            deleteRecordAlertDialog(deleteId, playerDao, it)
                        }
                    }
                }
            }
            // Set the LayoutManager that this RecyclerView will use.
            binding?.rvLeaderboard?.layoutManager = LinearLayoutManager(this)
            // adapter instance is set to the recyclerview to inflate the items.
            binding?.rvLeaderboard?.adapter = itemAdapter
            binding?.rvLeaderboard?.visibility = View.VISIBLE
        } else {

            binding?.rvLeaderboard?.visibility = View.GONE
            binding?.rvLeaderboard?.visibility = View.VISIBLE
        }
    }

    fun addRecord(playerDao: PlayerDao) {
        val name = "Adam"
        val points = 99
        if (name.isNotEmpty() && points!=null) {
            lifecycleScope.launch {
                playerDao.insert(PlayerEntity(name = name, points = points))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun deleteRecordAlertDialog(deleteId: Int, playerDao: PlayerDao, it: PlayerEntity) {

    }

    private fun updateRecordDialog(updateId: Int, playerDao: PlayerDao) {

    }

    override fun onDestroy()
    {
        super.onDestroy()
        binding = null
    }
}
