/*
 * Copyright (c) 2026 Andrii Pryladyshev.
 *              PROPRIETARY AND NON-COMMERCIAL SOURCE-AVAILABLE LICENSE
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to view
 * the source code and execute the Software solely for personal, non-commercial,
 * and educational purposes, subject to the following conditions:
 *
 * 1. OWNERSHIP: The Software and all intellectual property rights therein are
 *    and shall remain the sole and exclusive property of Andrii Pryladyshev.
 *
 * 2. RESTRICTIONS:
 *    - COMMERCIAL USE: You may not use the Software, or any portion thereof,
 *      for any commercial purposes, including but not limited to selling,
 *      leasing, or using it as part of a paid service.
 *    - MODIFICATION: You may not modify, adapt, transform, or create
 *      derivative works based upon the Software.
 *    - REDISTRIBUTION: You may not redistribute, publish, or host the
 *      Software on any other public platforms or repositories.
 *
 * 3. COPYRIGHT NOTICE: The above copyright notice and this permission notice
 *    shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
    //private lateinit var saveButton: MaterialButton

    private val viewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentViewSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appsAdapter: AppsListAdapter


    private var allInstalledApps: List<America> = emptyList()
    private lateinit var currentOverlay: OverlayConfig


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
        //saveButton = view.findViewById(R.id.save_btn)

        val contentTypeId = arguments?.getInt("contentTypeDataId")
        val overlayConfigId = arguments?.getInt("overlayConfigId")
        if (contentTypeId == null || overlayConfigId == null) return

        allAppsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        allAppsRecyclerView.setHasFixedSize(true)


        appsAdapter = AppsListAdapter(onItemClicked = {returnedShouldAdd, appToAdd ->
            viewModel.updateOverlayApps(currentOverlay, appToAdd, returnedShouldAdd)
        })
        allAppsRecyclerView.adapter = appsAdapter

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

//            val adapter = AppsListAdapter(onItemClicked = {returnedShouldAdd, appToAdd ->
//                viewModel.updateOverlayApps(overlay, appToAdd, returnedShouldAdd)
//            })
            appsAdapter.alreadyAddedApps = selectedApps.toMutableList()
            appsAdapter.isClickable = true
            appsAdapter.submitList(allInstalledApps)

        }
    }
}