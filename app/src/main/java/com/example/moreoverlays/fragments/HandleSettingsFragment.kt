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

//        val appsAdapter = AppsListAdapter()
//        binding.RVDiagonalUpSwipe.adapter = appsAdapter



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.overlayConfigs.collectLatest { list ->

                        overlayConfigsList = list.filter { it.side == side }

                        diagonalDownOverlayId = null
                        diagonalDownContentTypeId = null
                        straightSwipeOverlayId = null
                        straightSwipeContentTypeId = null
                        diagonalUpOverlayId = null
                        diagonalUpContentTypeId = null

                        for (overlayConfig in overlayConfigsList) {

                            var contentTypeId: Int? = null
                            var appsMutableList: MutableList<AppData> = mutableListOf()

                            for (contentType in overlayConfig.contentTypes) {
                                if (contentType is Apps) {
                                    contentTypeId = contentType.id
                                    appsMutableList = contentType.apps
                                }
                            }

                            when (overlayConfig.id) {
                                DOWN_SWIPE_RIGHT_SIDE_OVERLAY, DOWN_SWIPE_LEFT_SIDE_OVERLAY -> {
                                    diagonalDownOverlayId = overlayConfig.id
                                    diagonalDownContentTypeId = contentTypeId
                                    applyListAdapter(appsMutableList.toList(), overlayConfig.id)
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
                                }
                            }
                        }

                        setupClickListeners()

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
        val adapter = AppsListAdapter(false, mutableListOf<America>(), onItemClicked = { _, _ ->})
        when (id) {
            DOWN_SWIPE_RIGHT_SIDE_OVERLAY, DOWN_SWIPE_LEFT_SIDE_OVERLAY -> {
                binding.RVDiagonalDownSwipe.adapter = adapter
                binding.RVDiagonalDownSwipe.isNestedScrollingEnabled = false
                adapter.submitList(createAppData(itemList, requireContext()))
            }
            LEFT_SWIPE_OVERLAY, RIGHT_SWIPE_OVERLAY -> {
                binding.RVStraightSwipe.adapter = adapter
                binding.RVStraightSwipe.isNestedScrollingEnabled = false
                adapter.submitList(createAppData(itemList, requireContext()))
            }
            UP_SWIPE_RIGHT_SIDE_OVERLAY, UP_SWIPE_LEFT_SIDE_OVERLAY -> {
                binding.RVDiagonalUpSwipe.adapter = adapter
                binding.RVDiagonalUpSwipe.isNestedScrollingEnabled = false
                adapter.submitList(createAppData(itemList, requireContext()))
            }
        }
    }

    private fun setupClickListeners() {
        diagonalDownOverlayId?.let { overlayId ->
            diagonalDownContentTypeId?.let { contentTypeId ->
                binding.editDiagonalDownSwipe.setOnClickListener {
                    (activity as MainActivity).openViewSettingsFragment(overlayId, contentTypeId)
                }
            }
        }

        straightSwipeOverlayId?.let { overlayId ->
            straightSwipeContentTypeId?.let { contentTypeId ->
                binding.editStraightSwipe.setOnClickListener {
                    (activity as MainActivity).openViewSettingsFragment(overlayId, contentTypeId)
                }
            }
        }

        diagonalUpOverlayId?.let { overlayId ->
            diagonalUpContentTypeId?.let { contentTypeId ->
                binding.editDiagonalUpSwipe.setOnClickListener {
                    (activity as MainActivity).openViewSettingsFragment(overlayId, contentTypeId)
                }
            }
        }
    }

}