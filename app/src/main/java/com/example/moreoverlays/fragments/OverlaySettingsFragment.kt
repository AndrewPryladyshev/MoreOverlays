package com.example.moreoverlays.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.R
import com.example.moreoverlays.viewModels.MainActivityViewModel
import com.example.moreoverlays.activities.MainActivity
import com.example.moreoverlays.adapters.ViewsOnOverlayRecyclerViewAdapter
import com.example.moreoverlays.database.OverlayConfig
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class OverlaySettingsFragment : Fragment(R.layout.fragment_overlay_settings) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var floatingActionButton: FloatingActionButton

    private var currentOverlay: OverlayConfig? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val overlayId = arguments?.getInt("overlay_id")
//
//        recyclerView = view.findViewById(R.id.recyclerView)
//        floatingActionButton = view.findViewById(R.id.floatingActionButton)
//
//        if (overlayId != null) {
//            viewLifecycleOwner.lifecycleScope.launch {
//                viewModel.overlayConfigs.collectLatest { configsList ->
//                    val updatedOverlay = configsList.find { it.id == overlayId }
//
//                    if (updatedOverlay != null) {
//                        currentOverlay = updatedOverlay
//
//                        recyclerView.adapter = ViewsOnOverlayRecyclerViewAdapter(
//                            updatedOverlay.contentTypes,
//                            object : ViewsOnOverlayRecyclerViewAdapter.OnItemClickListener {
//                                override fun onClick(item: ContentTypeData) {
//                                    (activity as? MainActivity)?.openViewSettingsFragment(
//                                        item.id,
//                                        updatedOverlay.id,
//                                    )
//                                }
//
//                                override fun onLongClick(item: ContentTypeData): Boolean {
//                                    val popupView = createPopupView(requireContext())
//                                    val popupWindow = createPopupWindow(requireContext(), popupView, "")
//
//                                    val closeBtn: ImageButton = popupView.findViewById(R.id.closeBtn)
//                                    val title: EditText = popupView.findViewById(R.id.title)
//                                    val typeSelector: EditText = popupView.findViewById(R.id.type_selection)
//                                    val saveButton: Button = popupView.findViewById(R.id.save_btn)
//                                    val deleteImageButton = popupView.findViewById<ImageButton>(R.id.deleteButton)
//                                    deleteImageButton.visibility = View.VISIBLE
//
//                                    val titleText = item.title
//                                    val typeSelected = item.contentType
//
//                                    title.setText(titleText)
//                                    typeSelector.setText(typeSelected)
//
//                                    val hintColor = ContextCompat.getColor(requireContext(), R.color.light_grey)
//                                    val textColor = ContextCompat.getColor(requireContext(), R.color.white)
//
//                                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
//
//                                    saveButton.setOnClickListener {
//                                        val changedTitleText = title.text.toString()
//                                        val changedType = typeSelector.text.toString()
//
//                                        if (changedTitleText == titleText && changedType == typeSelected) {
//                                            popupWindow.dismiss()
//                                        }
//                                        else if (title.text.toString() != titleText || typeSelector.text.toString() != typeSelected ) {
//                                            viewModel.updateContentTypeOnOverlay(
//                                                currentOverlay!!.id,
//                                                changedTitleText,
//                                                it.id,
//                                                "update"
//                                            )
//                                        }
//                                    }
//
//                                    deleteImageButton.setOnClickListener {
//                                        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
//                                        val deleteContentTypePopupView = inflater.inflate(R.layout.delete_content_type_popup_layout, null)
//
//                                        val confirmButton = deleteContentTypePopupView.findViewById<Button>(R.id.confirmDeletionButton)
//                                        val cancelDeletionButton = deleteContentTypePopupView.findViewById<Button>(R.id.cancelDeletionButton)
//
//                                        val deletionPopupWindow = PopupWindow(
//                                            deleteContentTypePopupView,
//                                            ViewGroup.LayoutParams.WRAP_CONTENT,
//                                            ViewGroup.LayoutParams.WRAP_CONTENT,
//                                            true,
//                                        )
//
//                                        deletionPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
//
//                                        confirmButton.setOnClickListener {
//                                            viewModel.updateContentTypeOnOverlay(
//                                                currentOverlay!!.id,
//                                                title = item.title,
//                                                contentTypeId = item.id,
//                                                action = "delete",
//                                            )
//                                            deletionPopupWindow.dismiss()
//                                            popupWindow.dismiss()
//                                        }
//
//                                        cancelDeletionButton.setOnClickListener {
//                                            deletionPopupWindow.dismiss()
//                                        }
//                                    }
//                                    return true
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//        }
//
//        floatingActionButton.setOnClickListener {
//
//            val popupView = createPopupView(requireContext())
//            val popupWindow = createPopupWindow(requireContext(), popupView, "creation")
//
//            val closeBtn: ImageButton = popupView.findViewById(R.id.closeBtn)
//            val title: EditText = popupView.findViewById(R.id.title)
//            val saveButton: Button = popupView.findViewById(R.id.save_btn)
//            val deleteImageButton : ImageButton = popupView.findViewById(R.id.deleteButton)
//            val typeSelector: EditText = popupView.findViewById(R.id.type_selection)
//
//            deleteImageButton.visibility = View.GONE
//
//            val hintColor = ContextCompat.getColor(requireContext(), R.color.light_grey)
//            val textColor = ContextCompat.getColor(requireContext(), R.color.white)
//
//            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
//
//            closeBtn.setOnClickListener {
//                popupWindow.dismiss()
//            }
//
//            saveButton.setOnClickListener {
//                val titleText = title.text.toString()
//                val typeSelected = typeSelector.text.toString()
//
//                if (titleText.isNotBlank() && typeSelected.isNotBlank() && currentOverlay != null) {
//                    val currentCount = currentOverlay!!.contentTypes.size
//                    val maxLimit = 4
//
//                    if (currentCount >= maxLimit) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Maximum of $maxLimit content types reached for this overlay.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                        return@setOnClickListener
//                    }
//
//                    viewModel.addNewContentTypeToOverlay(
//                        currentOverlay!!.id,
//                        titleText,
//                        typeSelected
//                    )
//
//                    popupWindow.dismiss()
//                } else Toast.makeText(
//                    requireContext(),
//                    "name field or type select field is empty",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }
//    }
//
//    private fun createPopupWindow(context: Context, popupView: View, typeSelectorState: String) : PopupWindow {
//        val widthInDp = 280
//        val widthInPx = (widthInDp * resources.displayMetrics.density).toInt()
//
//        val popupWindow = PopupWindow(
//            popupView,
//            widthInPx,
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true
//        )
//
//
//        val typeSelector: EditText = popupView.findViewById(R.id.type_selection)
//
//        if (typeSelectorState == "creation") {
//            typeSelector.setOnClickListener {
//
//                val listPopup = createListPopup(context, typeSelector)
//
//                listPopup.isOutsideTouchable = true
//                listPopup.showAsDropDown(typeSelector)
//
//            }
//        }
//
//        return popupWindow
//    }
//
//    private fun createPopupView(context: Context) : View {
//        val inflater: LayoutInflater = LayoutInflater.from(context)
//        val popupView = inflater.inflate(R.layout.popup_layout, null)
//        return popupView
//    }
//
//    private fun createListPopup(context: Context, typeSelector: EditText) : PopupWindow {
//
//        val listOptions = mutableListOf("Apps", "Widgets", "Notes", "Photo", "None")
//
//        val listView = ListView(context)
//        val adapter =
//            ArrayAdapter(context, android.R.layout.simple_list_item_1, listOptions)
//        listView.adapter = adapter
//
//
//        val listPopup = PopupWindow(
//            listView,
//            (120 * resources.displayMetrics.density).toInt(),
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            true
//        )
//
//        listPopup.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                context,
//                R.drawable.bg_list_view
//            )
//        )
//
//
//        listView.setOnItemClickListener { _, _, position, _ ->
//            val selected = listOptions[position]
//            typeSelector.setText(selected)
//            listPopup.dismiss()
//            }
//
//        return listPopup
    }
}
