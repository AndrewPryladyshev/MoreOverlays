package com.example.moreoverlays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.animation.core.updateTransition
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
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

class ChildRecyclerViewAdapter(private val itemList: List<AppData>,
                               private val appsDao: AppsDao,
                               private val overlayConfig: OverlayConfig,

) : RecyclerView.Adapter<ChildRecyclerViewAdapter.ChildViewHolder>() {
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
                    currentItem.isSelected = true
                    CoroutineScope(Dispatchers.IO).launch {
                        appsDao.updateApps(listOf(currentItem))
                        val db = AppDatabase.getInstance(holder.itemView.context)
                        val overlayDao = db.daoOverlayConfigs()
                        val contentTypesList = overlayConfig.contentTypes
                        contentTypesList.forEach { item ->
                            when (item) {
                                is Apps -> {
                                    item.apps.add(currentItem)
                                }

                                is Notes -> TODO()
                                is Widgets -> TODO()
                            }
                        }
                    }
                }
                View.VISIBLE -> {
                    holder.checkImage.visibility = View.GONE
                    currentItem.isSelected = false
                    CoroutineScope(Dispatchers.IO).launch {
                        appsDao.updateApps(listOf(currentItem))
                        val db = AppDatabase.getInstance(holder.itemView.context)
                        val overlayDao = db.daoOverlayConfigs()
                        val contentTypesList = overlayConfig.contentTypes
                        contentTypesList.forEach { item ->
                            when (item) {
                                is Apps -> {
                                    item.apps.remove(currentItem)
                                }

                                is Notes -> TODO()
                                is Widgets -> TODO()
                            }
                        }
                    }
                }
                View.INVISIBLE -> {}
            }

        }

    }

    override fun getItemCount(): Int = itemList.size
}
