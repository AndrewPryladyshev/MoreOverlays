package com.example.moreoverlays.adapters

import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.America
import com.example.moreoverlays.R
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.utils.AppsDiffCallback

class AppsListAdapter(private var isClickable: Boolean,
                      private val alreadyAddedApps: MutableList<America>,
                      private val onItemClicked: (Boolean, AppData) -> Unit,

) : ListAdapter<America, AppsListAdapter.AppsViewHolder>(AppsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.apps_item, parent, false)
        return AppsViewHolder(view)
    }

    class AppsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIcon: ImageView = itemView.findViewById(R.id.iv_app_icon)
        val appName: TextView = itemView.findViewById(R.id.tv_app_name)
    }

    override fun onBindViewHolder(holder: AppsViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.appIcon.setImageBitmap(currentItem.icon)
        holder.appName.text = currentItem.appName

        holder.itemView.isClickable = isClickable

        val isAdded = alreadyAddedApps.any { it.packageName == currentItem.packageName }

        if (isAdded) {
            holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.purple_500))
        } else {
            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {

            val currentItemInAppData: AppData = AppData(currentItem.packageName, currentItem.appName)
            var shouldAdd: Boolean = true
            val isAlreadyAdded = alreadyAddedApps.any { it.packageName == currentItem.packageName }

            if (isAlreadyAdded) {
                alreadyAddedApps.removeIf { it.packageName == currentItem.packageName }
                holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
                onItemClicked(false, currentItemInAppData)

            } else {
                alreadyAddedApps.add(currentItem)
                holder.itemView.setBackgroundColor(holder.itemView.context.getColor(R.color.purple_500))
                onItemClicked(true, currentItemInAppData)

            }

        }

    }

}