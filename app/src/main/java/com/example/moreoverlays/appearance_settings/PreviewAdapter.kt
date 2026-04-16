package com.example.moreoverlays.appearance_settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.utils.MAIN
import com.example.moreoverlays.appearance_settings.PreviewDiffCallback
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily

class PreviewAdapter : ListAdapter<OverlayPreviewState, PreviewAdapter.ViewHolder>(
    PreviewDiffCallback()
) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leftView: MaterialCardView = itemView.findViewById(R.id.previewLeft)
        val rightView: MaterialCardView = itemView.findViewById(R.id.previewRight)

        fun updateOverlayVisibility(visible: String) {
            rightView?.visibility = if (visible == "right" || visible == "both") View.VISIBLE else View.GONE
            leftView?.visibility = if (visible == "left" || visible == "both") View.VISIBLE else View.GONE

            rightView?.alpha = 0.5f
            leftView?.alpha = 0.5f
        }

//        fun updateOverlayPreview(side: String, alpha: Float) {
//
//            rightView?.let {
//                if (side == "right" || side == "both") {
//                    it.visibility = View.VISIBLE
//                    it.alpha = alpha
//                } else {
//                    it.visibility = View.INVISIBLE
//                }
//
//            }
//
//            leftView?.let {
//                if (side == "left" || side == "both") {
//                    it.visibility = View.VISIBLE
//                    it.alpha = alpha
//                } else {
//                    it.visibility = View.INVISIBLE
//                }
//
//
//            }
//
////            rightView?.visibility = if (side == "right" || visible == "both") View.VISIBLE else View.INVISIBLE
////            leftView?.visibility = if (visible == "left" || visible == "both") View.VISIBLE else View.INVISIBLE
////
////            rightView?.alpha = 0.5f
////            leftView?.alpha = 0.5f
//        }
//
//        fun bind(data: OverlayPreviewState){
//            updateOverlayPreview(data.displayMode, data.opacity)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = inflater.inflate(viewType, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

//        holder.bind(item)
//        holder.updateOverlayPreview(item.displayMode, item.opacity)

        holder.updateOverlayVisibility(item.displayMode)
        holder.leftView.alpha = item.opacity
        holder.rightView.alpha = item.opacity

        val radiusInPx = item.cornerSize * holder.itemView.context.resources.displayMetrics.density

        holder.rightView.radius = radiusInPx
        holder.leftView.radius = radiusInPx

        holder.rightView.shapeAppearanceModel = holder.rightView.shapeAppearanceModel.toBuilder()
            .setTopLeftCorner(CornerFamily.ROUNDED, radiusInPx)
            .setBottomLeftCorner(CornerFamily.ROUNDED, radiusInPx)
            .setTopRightCorner(CornerFamily.ROUNDED, 0f)
            .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
            .build()

        holder.leftView.shapeAppearanceModel = holder.leftView.shapeAppearanceModel.toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, radiusInPx)
            .setBottomRightCorner(CornerFamily.ROUNDED, radiusInPx)
            .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
            .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
            .build()
    }


    override fun getItemViewType(position: Int): Int {
        val config = getItem(position)

        return when(config.id) {
            MAIN -> { R.layout.item_preview_main_right }
            else -> {
                R.layout.item_preview_overlays }
        }
    }

//    fun updateVisibility(onlyVisible: String, position: Int) {
//        currentVisibility = onlyVisible
//    }

}