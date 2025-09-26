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

class ViewsOnOverlayRecyclerViewAdapter(
    private var items: List<ContentTypeData>,
    //private val listener: RootOverlaysRecyclerViewAdapter.OnItemClickListener
) : RecyclerView.Adapter<ViewsOnOverlayRecyclerViewAdapter.OverlayViewsViewHolder>() {


    interface OnItemClickListener {
        fun onClick(id: Int){}
    }

    inner class OverlayViewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = itemView.findViewById(R.id.text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverlayViewsViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return OverlayViewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: OverlayViewsViewHolder, position: Int) {
        val currentItem = items[position]

        when (currentItem) {
            is Apps -> {
                val appsId = currentItem.id // <- вот он, id внутри класса Apps
                holder.textView.text = "$appsId"
            }
            is Widgets -> {
                val widgetId = currentItem.id
                holder.textView.text = "$widgetId"
            }
            is Notes -> {
                val noteId = currentItem.id
                holder.textView.text = "$noteId"
            }
        }
        holder.textView.text = currentItem.toString()
    }

    override fun getItemCount(): Int = items.size
}