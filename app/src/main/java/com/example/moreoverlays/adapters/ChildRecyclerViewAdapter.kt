package com.example.moreoverlays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.database.AppData

class ChildRecyclerViewAdapter(private val itemList: List<AppData>) : RecyclerView.Adapter<ChildRecyclerViewAdapter.ChildViewHolder>() {
    var selectedApps = mutableListOf<AppData>()

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.icon)
        val text: TextView = itemView.findViewById(R.id.name)
        val checkImage: ImageView = itemView.findViewById(R.id.ckeck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.apps_item, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.text.text = currentItem.appName
        holder.image.setImageDrawable(holder.image.context.packageManager.getApplicationIcon(currentItem.appPackage))

        holder.itemView.setOnClickListener {
            when (holder.checkImage.visibility) {
                View.GONE -> {
                    holder.checkImage.visibility = View.VISIBLE
                    selectedApps.add(currentItem)
                }
                View.VISIBLE -> {
                    if (currentItem in selectedApps) {
                        holder.checkImage.visibility = View.GONE
                        selectedApps.remove(currentItem)
                    }
                }
                View.INVISIBLE -> {}
            }
            //holder.checkImage.visibility = if (holder.checkImage.visibility == View.GONE) View.VISIBLE else View.GONE

        }

    }

    override fun getItemCount(): Int = itemList.size


}
