package com.example.moreoverlays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.Notes
import com.example.moreoverlays.R
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.database.OverlayConfig

class ViewsOnOverlayRecyclerViewAdapter(
    private var items: List<ContentTypeData>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ViewsOnOverlayRecyclerViewAdapter.OverlayViewsViewHolder>() {


    interface OnItemClickListener {
        fun onClick(item: ContentTypeData){}
        fun onLongClick(item: ContentTypeData): Boolean
    }

    inner class OverlayViewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = itemView.findViewById(R.id.text)
        fun bind(item: ContentTypeData) {
            itemView.setOnClickListener {
                listener.onClick(item)
            }
            itemView.setOnLongClickListener {
                listener.onLongClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverlayViewsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return OverlayViewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OverlayViewsViewHolder, position: Int) {
        val currentItem = items[position]

        when (currentItem) {
            is Apps -> {
                val appsId = currentItem.id
                holder.textView.text = currentItem.title
            }
            is Widgets -> {
                val widgetId = currentItem.id
                holder.textView.text = currentItem.title
            }
            is Notes -> {
                val noteId = currentItem.id
                holder.textView.text = currentItem.title
            }
        }

        holder.bind(currentItem)

    }

    override fun getItemCount(): Int = items.size
}