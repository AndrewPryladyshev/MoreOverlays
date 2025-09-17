package com.example.moreoverlays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R

class ParentRecyclerViewAdapter(private val items: List<Parent>) : RecyclerView.Adapter<ParentRecyclerViewAdapter.ParentViewHolder>() {

    class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.button)
        val recyclerView: RecyclerView = itemView.findViewById(R.id.childRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.parent_item, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val currentItem = items[position]

        holder.button.text = currentItem.text
        holder.recyclerView.adapter = ChildRecyclerViewAdapter(currentItem.appList)

        holder.recyclerView.apply {
            layoutManager = GridLayoutManager(holder.recyclerView.context, 4)
            isNestedScrollingEnabled = false
            isVisible = currentItem.isExpanded
        }

        holder.button.setOnClickListener {
            currentItem.isExpanded = !currentItem.isExpanded
            notifyItemChanged(position)
        }


    }

    override fun getItemCount(): Int = items.size
}
