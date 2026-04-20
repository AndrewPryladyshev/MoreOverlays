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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.America
import com.example.moreoverlays.Apps
import com.example.moreoverlays.ContentTypeData
import com.example.moreoverlays.R
import com.example.moreoverlays.activities.MainActivity
import com.example.moreoverlays.adapters.AppsListAdapter
import com.example.moreoverlays.database.AppData
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.databinding.HandleSettingsFragmentBinding
import com.example.moreoverlays.utils.DOWN_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.DOWN_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN_OVERLAY_LEFT
import com.example.moreoverlays.utils.MAIN_OVERLAY_RIGHT
import com.example.moreoverlays.utils.RIGHT_SIDE
import com.example.moreoverlays.utils.RIGHT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_LEFT_SIDE_OVERLAY
import com.example.moreoverlays.utils.UP_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.createAppData
import com.example.moreoverlays.viewModels.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class HandleSettingsFragment : Fragment(R.layout.handle_settings_fragment) {

    private val viewModel: MainActivityViewModel by activityViewModels()
    private var _binding: HandleSettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private var side: Int = 0
    private lateinit var overlayConfigsList: List<OverlayConfig>
    private lateinit var allAppsList: List<America>

    private var diagonalDownOverlayId: Int? = null
    private var diagonalDownContentTypeId: Int? = null

    private var straightSwipeOverlayId: Int? = null
    private var straightSwipeContentTypeId: Int? = null

    private var diagonalUpOverlayId: Int? = null
    private var diagonalUpContentTypeId: Int? = null

    private val diagonalDownAdapter = AppsListAdapter(onItemClicked = { _, _ ->})
    private val straightSwipeAdapter = AppsListAdapter(onItemClicked = { _, _ ->})
    private val diagonalUpAdapter = AppsListAdapter(onItemClicked = { _, _ ->})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HandleSettingsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        side = arguments?.getInt("side") ?: 0
        val materialSwitch = binding.switchMasterToggle



        binding.RVDiagonalDownSwipe.adapter = diagonalDownAdapter
        binding.RVStraightSwipe.adapter = straightSwipeAdapter
        binding.RVDiagonalUpSwipe.adapter = diagonalUpAdapter

        binding.RVDiagonalDownSwipe.isNestedScrollingEnabled = false
        binding.RVStraightSwipe.isNestedScrollingEnabled = false
        binding.RVDiagonalUpSwipe.isNestedScrollingEnabled = false



        var mainOverlayConfigId = RIGHT_SIDE
        if (side == RIGHT_SIDE) {
            mainOverlayConfigId = MAIN_OVERLAY_RIGHT
            binding.sideText.text = "Enable Right Detector"
        } else {
            mainOverlayConfigId = MAIN_OVERLAY_LEFT
            binding.sideText.text = "Enable Left Detector"
        }


//        val appsAdapter = AppsListAdapter()
//        binding.RVDiagonalUpSwipe.adapter = appsAdapter



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.overlayConfigs.collectLatest { list ->

                        Log.d("CONFIG_DIAG", "1. Total configs received: ${list.size}")
                        Log.d("CONFIG_DIAG", "   Target side value: $side")

                        val mainOverlayConfig = list.find { it.id == mainOverlayConfigId }

                        overlayConfigsList = list.filter { it.side == side }

                        mainOverlayConfig?.let {
                            materialSwitch.setOnCheckedChangeListener(null)
                            materialSwitch.isChecked = it.isEnabled
                            materialSwitch.setOnCheckedChangeListener { _, isChecked ->
                                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                                    viewModel.updateOverlayVisibility(mainOverlayConfigId, isChecked)
                                    Log.d("HANDLE_CHECK_DEBUG", "$isChecked")
                                }
                            }
                        }


//                        diagonalDownOverlayId = null
//                        diagonalDownContentTypeId = null
//                        straightSwipeOverlayId = null
//                        straightSwipeContentTypeId = null
//                        diagonalUpOverlayId = null
//                        diagonalUpContentTypeId = null

                        for (overlayConfig in overlayConfigsList) {
                            Log.d("CONFIG_DEBUG", "Processing ID: ${overlayConfig.id}")
                            val appsContentType = overlayConfig.contentTypes
                                .filterIsInstance<Apps>()
                                .firstOrNull()

                            if (appsContentType == null) {
                                Log.w("CONFIG_DEBUG", "Overlay ${overlayConfig.id} has NO Apps content.")
                            }

                            val contentTypeId: Int? = appsContentType?.id
                            val appsMutableList: List<AppData> =
                                appsContentType?.apps?.toList() ?: emptyList()
                            if (contentTypeId != null) {
                                when (overlayConfig.id) {
                                    DOWN_SWIPE_RIGHT_SIDE_OVERLAY, DOWN_SWIPE_LEFT_SIDE_OVERLAY -> {
                                        diagonalDownOverlayId = overlayConfig.id
                                        diagonalDownContentTypeId = contentTypeId
                                        applyListAdapter(appsMutableList.toList(), overlayConfig.id)
                                        Log.d("CONFIG_DEBUG", "DOWN SWIPE IDs set: ${overlayConfig.id}, $contentTypeId. App count: ${appsMutableList.size}")
                                    }

                                    LEFT_SWIPE_OVERLAY, RIGHT_SWIPE_OVERLAY -> {
                                        straightSwipeOverlayId = overlayConfig.id
                                        straightSwipeContentTypeId = contentTypeId
                                        applyListAdapter(appsMutableList.toList(), overlayConfig.id)

                                    }

                                    UP_SWIPE_RIGHT_SIDE_OVERLAY, UP_SWIPE_LEFT_SIDE_OVERLAY -> {
                                        diagonalUpOverlayId = overlayConfig.id
                                        diagonalUpContentTypeId = contentTypeId
                                        applyListAdapter(appsMutableList.toList(), overlayConfig.id)
                                        Log.d("CONFIG_DEBUG", "UP SWIPE IDs set: ${overlayConfig.id}, $contentTypeId. App count: ${appsMutableList.size}")
                                    }
                                }
                            }
                        }
                        setupClickListeners()
                        Log.d("CONFIG_DEBUG", "--- setupClickListeners called. ---")
                    }
                }

                launch {
                    viewModel.americaInstalledApps.collectLatest { l ->
                        allAppsList = l
                    }
                }
            }
        }
    }

    private fun applyListAdapter(itemList: List<AppData>, id: Int) {
        when (id) {
            DOWN_SWIPE_RIGHT_SIDE_OVERLAY, DOWN_SWIPE_LEFT_SIDE_OVERLAY -> {
                diagonalDownAdapter.submitList(
                    createAppData(itemList, requireContext())
                )
            }
            LEFT_SWIPE_OVERLAY, RIGHT_SWIPE_OVERLAY -> {
                straightSwipeAdapter.submitList(
                    createAppData(itemList, requireContext())
                )
            }
            UP_SWIPE_RIGHT_SIDE_OVERLAY, UP_SWIPE_LEFT_SIDE_OVERLAY -> {
                diagonalUpAdapter.submitList(
                    createAppData(itemList, requireContext())
                )
            }
        }
    }

    private fun setupClickListeners() {
        if (diagonalDownOverlayId != null) {
            Log.d("CLICK_DEBUG", "Setting Down Swipe Listener for OID: $diagonalDownOverlayId")
            binding.editDiagonalDownSwipe.setOnClickListener {
                (activity as MainActivity).openViewSettingsFragment(diagonalDownOverlayId!!, diagonalDownContentTypeId!!)
            }
        } else {
            Log.e("CLICK_DEBUG", "Down Swipe Listener NOT set. ID is NULL.")
        }

        if (straightSwipeOverlayId != null) {
            Log.d("CLICK_DEBUG", "Setting Straight Swipe Listener for OID: $straightSwipeOverlayId")
            binding.editStraightSwipe.setOnClickListener {
                (activity as MainActivity).openViewSettingsFragment(straightSwipeOverlayId!!, straightSwipeContentTypeId!!)
            }
        } else {
            Log.e("CLICK_DEBUG", "Straight Swipe Listener NOT set. ID is NULL.")
        }

        if (diagonalUpOverlayId != null) {
            Log.d("CLICK_DEBUG", "Setting Up Swipe Listener for OID: $diagonalUpOverlayId")
            binding.editDiagonalUpSwipe.setOnClickListener {
                (activity as MainActivity).openViewSettingsFragment(diagonalUpOverlayId!!, diagonalUpContentTypeId!!)
                Log.d("DEBUG", "$diagonalUpOverlayId, $diagonalUpContentTypeId")
            }
        } else {
            Log.e("CLICK_DEBUG", "Up Swipe Listener NOT set. ID is NULL.")
        }
    }
}