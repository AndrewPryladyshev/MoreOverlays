package com.example.moreoverlays.adapters

import android.text.BoringLayout
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.America
import com.example.moreoverlays.R
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.utils.AppsDiffCallback

class AppsListAdapter(private val onItemClicked: (Boolean, AppData) -> Unit, ) : ListAdapter<America, AppsListAdapter.AppsViewHolder>(AppsDiffCallback()) {

    var isClickable: Boolean = false
    var alreadyAddedApps: MutableList<America> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : AppsViewHolder {
//        val themedContext = ContextThemeWrapper(
//            parent.context,
//            R.style.Theme_MoreOverlays
//        )

        val view = LayoutInflater.from(ContextThemeWrapper(parent.context, R.style.Theme_MoreOverlays))
            .inflate(R.layout.apps_item, parent, false)

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

        holder.itemView.isSelected = isAdded
        holder.itemView.setBackgroundResource(R.drawable.test_background)

//        if (isAdded) {
//            holder.itemView.setBackgroundResource(R.drawable.test_background)
//        } else {
//            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
//        }

        var isClickableForClickable = true
        val currentItemInAppData = AppData(currentItem.packageName, currentItem.appName)



        if (holder.itemView.isClickable) {

            holder.itemView.setOnClickListener {

//                val currentItemInAppData: AppData = AppData(currentItem.packageName, currentItem.appName)
//
//                if (isAdded) {
//                    alreadyAddedApps.removeIf { it.packageName == currentItem.packageName }
//                    holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
//                    onItemClicked(false, currentItemInAppData)
//
//                } else {
//                    alreadyAddedApps.add(currentItem)
//                    holder.itemView.setBackgroundResource(R.drawable.test_background)
//                    onItemClicked(true, currentItemInAppData)
//
//                }

                if (alreadyAddedApps.size < 4) {
                    isClickableForClickable = true
                }
                else if (alreadyAddedApps.size == 4) {
                    isClickableForClickable = false
                }


                val currentlyAdded = alreadyAddedApps.any { it.packageName == currentItem.packageName }

                if (currentlyAdded) {
                    alreadyAddedApps.removeIf { it.packageName == currentItem.packageName }
                    holder.itemView.isSelected = false
                    onItemClicked(false, currentItemInAppData)
                } else if (isClickableForClickable) {
                    alreadyAddedApps.add(currentItem)
                    holder.itemView.isSelected = true
                    onItemClicked(true, currentItemInAppData)
                }
                else {
                    Toast.makeText(holder.itemView.context, "Currently the limit of apps is 4", Toast.LENGTH_SHORT).show()
                }


            }
        }


    }

}