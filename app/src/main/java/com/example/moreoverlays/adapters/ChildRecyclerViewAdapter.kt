package com.example.moreoverlays.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.animation.core.updateTransition
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.America
import com.example.moreoverlays.Apps
import com.example.moreoverlays.Notes
import com.example.moreoverlays.R
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.database.AppsDao
import com.example.moreoverlays.database.OverlayConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChildRecyclerViewAdapter(private val itemList: MutableList<America>,
//                               private val appsDao: AppsDao,
//                               private var overlayConfig: OverlayConfig,
                               private val onItemClicked: (AppData) -> Unit,
                               private var isSelectionMode: Boolean,

                               ) : RecyclerView.Adapter<ChildRecyclerViewAdapter.ChildViewHolder>() {

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_app_icon)
        val text: TextView = itemView.findViewById(R.id.tv_app_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.apps_item, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.text.text = currentItem.appName


        val icon = currentItem.icon
        holder.image.setImageBitmap(icon)

        holder.itemView.setOnClickListener {
            val currentItemInAppData: AppData = AppData(currentItem.packageName, currentItem.appName)

            onItemClicked(currentItemInAppData)
        }
    }

    override fun getItemCount(): Int = itemList.size
}
