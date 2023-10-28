package com.example.myapplication.roomDatabase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemsRowBinding

class ItemAdapter(
    private val items: ArrayList<PlayerEntity>,
    private val updateListener: (id: Int) -> Unit,
    private val deleteListener: (id: Int) -> Unit
): RecyclerView.Adapter<ItemAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvRank.text = (position+1).toString()
        holder.tvPlayerName.text = item.name
        holder.tvPoints.text = item.points.toString()
    }

    class ViewHolder(binding: ItemsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        // Holds the TextView that will add each item to
        val tvRank = binding.tvRank
        var tvPlayerName = binding.tvPlayerName
        val tvPoints = binding.tvPoints
    }

}

