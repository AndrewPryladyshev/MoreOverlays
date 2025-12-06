package com.example.moreoverlays.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.adapters.ChildRecyclerViewAdapter
import com.example.moreoverlays.database.AppData
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.viewModels.MainActivityViewModel
import com.example.moreoverlays.database.OverlayConfig
import kotlinx.coroutines.flow.collectLatest
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.moreoverlays.America
import com.example.moreoverlays.adapters.AppsListAdapter
import com.example.moreoverlays.databinding.FragmentViewSettingsBinding
import com.example.moreoverlays.utils.createAppData
import kotlinx.coroutines.flow.combine

class ViewSettingsFragment : Fragment(R.layout.fragment_view_settings) {

    private lateinit var selectedAppsRecyclerView: RecyclerView
    private lateinit var allAppsRecyclerView: RecyclerView
    private lateinit var saveButton: MaterialButton

    private val viewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentViewSettingsBinding? = null
    private val binding get() = _binding!!


    private var allInstalledApps: List<America> = emptyList()
    private var currentOverlay: OverlayConfig? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allAppsRecyclerView = view.findViewById(R.id.allAppsRV)
        saveButton = view.findViewById(R.id.save_btn)

        val contentTypeId = arguments?.getInt("contentTypeDataId")
        val overlayConfigId = arguments?.getInt("overlayConfigId")
        if (contentTypeId == null || overlayConfigId == null) return

        allAppsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.americaInstalledApps
                    .combine(viewModel.overlayConfigs) { apps, configs -> Pair(apps, configs) }
                    .collectLatest { (appsList, configs) ->
                        allInstalledApps = appsList

                        val foundOverlay = configs.find { it.id == overlayConfigId }
                        if (foundOverlay != null) {
                            currentOverlay = foundOverlay
                            updateRecyclerViews(contentTypeId, foundOverlay)
                        } else {
                            allAppsRecyclerView.adapter = ChildRecyclerViewAdapter(
                                mutableListOf(), isSelectionMode = false, onItemClicked = {}
                            )
                        }
                    }
            }
        }
    }

    private fun updateRecyclerViews(contentTypeId: Int, overlay: OverlayConfig) {

        Log.d("VSF", "updateRecyclerViews called for overlay: ${overlay.id}")
        Log.d("VSF", "allInstalledApps size: ${allInstalledApps.size}")

        val currentViewData = overlay.contentTypes.find { it.id == contentTypeId }

        if (currentViewData is Apps) {

            val selectedApps: List<America> = createAppData(currentViewData.apps, requireContext())

//            selectedAppsRecyclerView.adapter = ChildRecyclerViewAdapter(
//                itemList = selectedApps.toMutableList(),
//                isSelectionMode = true,
//                onItemClicked = { appToRemove ->
//                    viewModel.updateOverlayApps(overlay, appToRemove, shouldAdd = false)
//                }
//            )

//            val availableApps = allInstalledApps.toMutableList().apply {
//                removeAll { all ->
//                    selectedApps.any { selected -> selected.packageName == all.packageName }
//                }
//            }

//            allAppsRecyclerView.adapter = ChildRecyclerViewAdapter(
//                itemList = availableApps,
//                isSelectionMode = false,
//                onItemClicked = { appToAdd ->
//                    viewModel.updateOverlayApps(overlay, appToAdd, shouldAdd = true)
//                }
//            )

            val adapter = AppsListAdapter(true, selectedApps.toMutableList(), onItemClicked = {returnedShouldAdd, appToAdd ->
                viewModel.updateOverlayApps(overlay, appToAdd, returnedShouldAdd)
            })
            adapter.submitList(allInstalledApps)
            allAppsRecyclerView.adapter = adapter
            allAppsRecyclerView.setHasFixedSize(true)

        } else {

//            selectedAppsRecyclerView.adapter = ChildRecyclerViewAdapter(mutableListOf(), isSelectionMode = true, onItemClicked = {})
            allAppsRecyclerView.adapter = ChildRecyclerViewAdapter(mutableListOf(), isSelectionMode = false, onItemClicked = {})
        }
    }
}