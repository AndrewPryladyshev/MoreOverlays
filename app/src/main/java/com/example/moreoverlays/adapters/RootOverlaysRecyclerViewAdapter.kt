package com.example.moreoverlays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.database.OverlayConfig


class RootOverlaysRecyclerViewAdapter(
    private var itemsList: List<OverlayConfig>,
    private val listener: OnItemClickListener) : RecyclerView.Adapter<RootOverlaysRecyclerViewAdapter.MainOverlaysViewHolder>() {


    interface OnItemClickListener {
        fun onItemClick(item: OverlayConfig)
    }

    inner class MainOverlaysViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: OverlayConfig) {
            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
        val textView: TextView = itemView.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainOverlaysViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MainOverlaysViewHolder(view)
    }

    override fun getItemCount(): Int = itemsList.size

    override fun onBindViewHolder(holder: MainOverlaysViewHolder, position: Int) {
        val currentItem = itemsList[position]

        holder.bind(currentItem)
        holder.textView.text = currentItem.name

    }

    fun updateList(newList: List<OverlayConfig>) {
        this.itemsList = newList
        notifyDataSetChanged()
    }
}