package com.example.moreoverlays.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.OverlayPreviewState
import com.example.moreoverlays.R
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.utils.MAIN
import com.example.moreoverlays.utils.MAIN_OVERLAY_RIGHT
import com.example.moreoverlays.utils.PreviewDiffCallback

class PreviewAdapter : ListAdapter<OverlayPreviewState, PreviewAdapter.ViewHolder>(PreviewDiffCallback()) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val leftView: View? = itemView.findViewById(R.id.previewLeft)
        private val rightView: View? = itemView.findViewById(R.id.previewRight)

//        fun updateOverlayVisibility(visible: String) {
//            rightView?.visibility = if (visible == "right" || visible == "both") View.VISIBLE else View.GONE
//            leftView?.visibility = if (visible == "left" || visible == "both") View.VISIBLE else View.GONE
//
//            rightView?.alpha = 0.5f
//            leftView?.alpha = 0.5f
//        }
        fun updateOverlayPreview(side: String, alpha: Float) {

            rightView?.let {
                if (side == "right" || side == "both") {
                    it.visibility = View.VISIBLE
                    it.alpha = alpha
                } else {
                    it.visibility = View.INVISIBLE
                }

            }

            leftView?.let {
                if (side == "left" || side == "both") {
                    it.visibility = View.VISIBLE
                    it.alpha = alpha
                } else {
                    it.visibility = View.INVISIBLE
                }


            }

//            rightView?.visibility = if (side == "right" || visible == "both") View.VISIBLE else View.INVISIBLE
//            leftView?.visibility = if (visible == "left" || visible == "both") View.VISIBLE else View.INVISIBLE
//
//            rightView?.alpha = 0.5f
//            leftView?.alpha = 0.5f
        }

        fun bind(data: OverlayPreviewState){
            updateOverlayPreview(data.displayMode, data.opacity)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
        holder.updateOverlayPreview(item.displayMode, item.opacity)

    }


    override fun getItemViewType(position: Int): Int {
        val config = getItem(position)

        return when(config.id) {
            MAIN -> { R.layout.item_preview_main_right }
            else -> {R.layout.item_preview_overlays }
        }
    }

//    fun updateVisibility(onlyVisible: String, position: Int) {
//        currentVisibility = onlyVisible
//    }

}