package com.example.moreoverlays.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.adapters.ChildRecyclerViewAdapter
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.AppDatabase
import com.example.moreoverlays.utils.getInstalledApps
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.SharedPreferences
import android.util.Log
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.Notes
import com.example.moreoverlays.ViewModels.MainActivityViewModel
import com.example.moreoverlays.Widgets
import com.example.moreoverlays.database.OverlayConfig
import kotlinx.coroutines.flow.collectLatest
import androidx.fragment.app.activityViewModels

class ViewSettingsFragment : Fragment(R.layout.fragment_view_settings) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadedApps: List<AppData>
    private lateinit var saveButton: MaterialButton

    private val viewModel: MainActivityViewModel by activityViewModels()
    private val appsDao by lazy { AppDatabase.getInstance(requireContext()).daoApps() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        saveButton = view.findViewById(R.id.save_btn)

        val viewData = arguments?.getParcelable<ContentTypeData>("overlay_view")
        val overlayData = arguments?.getParcelable<OverlayConfig>("overlay_data")


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.installedApps.collectLatest { appsList ->
                when (viewData) {
                    is Apps -> {
                        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
                        if (overlayData != null) {
                        recyclerView.adapter = ChildRecyclerViewAdapter(appsList, appsDao, overlayData)
                        }
                    }

                    is Notes -> TODO()
                    is Widgets -> TODO()
                    null -> TODO()
                }
            }
        }
    }
}