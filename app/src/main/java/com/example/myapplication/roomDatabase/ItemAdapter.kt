package com.example.myapplication.roomDatabase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemsRowBinding

class ItemAdapter(
    private val items: ArrayList<PlayerEntity>
): RecyclerView.Adapter<ItemAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.tvRank.text = (position+1).toString()
        holder.tvPlayerName.text = item.name
        holder.tvPoints.text = item.points.toString()

        // Updating the background color according to the odd/even positions in list.
        if (position % 2 == 0) {
            holder.llMain.background = ContextCompat.getDrawable(context, R.drawable.recycler_view_item_bg_v2)
        } else {
            holder.llMain.background = ContextCompat.getDrawable(context, R.drawable.recycler_view_item_bg)
        }
    }

    class ViewHolder(binding: ItemsRowBinding) : RecyclerView.ViewHolder(binding.root) {
        // Holds the TextView that will add each item to
        val tvRank = binding.tvRank
        var tvPlayerName = binding.tvPlayerName
        val tvPoints = binding.tvPoints
        val llMain = binding.llMain
    }

}

