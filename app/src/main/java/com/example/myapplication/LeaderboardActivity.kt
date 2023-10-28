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


        val employeeDao = (application as PlayerApp).db.playerDao()

        lifecycleScope.launch {
            employeeDao.fetchAllEmployee().collect {
                Log.d("exactplayer", "$it")
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list, employeeDao)
            }
        }

    }

    private fun setupListOfDataIntoRecyclerView(playerList: ArrayList<PlayerEntity>, employeeDao: PlayerDao) {
        if (playerList.isNotEmpty()) {
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(playerList,{updateId ->
                updateRecordDialog(updateId,employeeDao)
            }){ deleteId->
                lifecycleScope.launch {
                    employeeDao.fetchEmployeeById(deleteId).collect {
                        if (it != null) {
                            deleteRecordAlertDialog(deleteId, employeeDao, it)
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

    private fun deleteRecordAlertDialog(deleteId: Int, employeeDao: PlayerDao, it: PlayerEntity) {

    }

    private fun updateRecordDialog(updateId: Int, employeeDao: PlayerDao) {

    }
}
