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

package com.example.moreoverlays.appearance_settings

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.moreoverlays.R
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.databinding.FragmentAppearanceBinding
import com.example.moreoverlays.databinding.LayoutTopSheetBinding
import com.example.moreoverlays.external.mytopsheet.TopSheetDialog
import com.example.moreoverlays.utils.MAIN_OVERLAY_LEFT
import com.example.moreoverlays.utils.MAIN_OVERLAY_RIGHT
import com.example.moreoverlays.viewModels.MainActivityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppearanceFragment : Fragment(R.layout.fragment_appearance) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentAppearanceBinding? = null
    private val binding get() = _binding!!
    private var rightMainOverlay: OverlayConfig? = null
    private var leftMainOverlay: OverlayConfig? = null

    private var dX = 0f
    private var dY = 0f

//    private lateinit var pagerAdapter: PreviewAdapter
    private var currentOverlayIds = listOf(MAIN_OVERLAY_LEFT, MAIN_OVERLAY_RIGHT)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppearanceBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.leftDetector.isClickable = true
//        binding.rightDetector.isClickable = true
//        binding.leftDetector.isClickable = false
//        binding.rightDetector.isClickable = false

//        val pager = binding.previewPager
//        pagerAdapter = PreviewAdapter()
//        pager.adapter = pagerAdapter

//        val currentItem = pagerAdapter.currentList.getOrNull(pager.currentItem)

//        val dialog = TopSheetDialog(requireContext())
//        val sheetBinding = LayoutTopSheetBinding.inflate(layoutInflater)
//
//        dialog.setContentView(sheetBinding.root)
//        dialog.setOwnerActivity(requireActivity())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            dialog.window?.attributes?.layoutInDisplayCutoutMode =
//                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//        }
//
//        dialog.window?.let { window ->
//            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
//        }
//
//        dialog.show()

//        val statusBarSpacer = sheetBinding.statusBarSpacer
//
//        val insets = ViewCompat.getRootWindowInsets(requireView())
//        val statusBarHeight = insets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0
//
//        if (statusBarSpacer.layoutParams.height != statusBarHeight) {
//            statusBarSpacer.layoutParams.height = statusBarHeight
//            statusBarSpacer.requestLayout()
//        }

        binding.leftDetector.setOnClickListener {
            Log.d("DebugHIHIHIHA", "clicking...")
        }



//        binding.toggleSideSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
//            if (isChecked) {
//                val side = when (checkedId) {
//                    R.id.btnLeft -> "left"
//                    R.id.btnBoth -> "both"
//                    else -> "right"
//                }
//                updateLivePreview(side)
//            }
//        }

//        binding.previewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                val item = pagerAdapter.currentList.getOrNull(position)
//                item?.let {
//                    val buttonId = when(it.displayMode) {
//                        "left" -> R.id.btnLeft
//                        "both" -> R.id.btnBoth
//                        else -> R.id.btnRight
//                    }
//                    currentOverlayIds = item.overlayIds
//
//                    binding.toggleSideSelector.check(buttonId)
//                }
//            }
//        })

        binding.layout.layout.sliderOpacity.setLabelFormatter { value ->
            if (value % 1 == 0f) {
                value.toInt().toString()
            } else {
                "%.1f".format(value).replace(",", ".")
            }
        }

        binding.layout.layout.sliderWidth.setLabelFormatter { value ->
            "${value.toInt()}%"
        }

//        binding.leftDetector.alpha = binding.sliderOpacity.value
//        binding.rightDetector.alpha = binding.sliderOpacity.value

        binding.layout.layout.sliderOpacity.addOnChangeListener {slider, value, fromUser ->
            changeOpacity(value)
        }

        binding.layout.layout.sliderCornerRadius.addOnChangeListener { slider,value,fromUser ->
            changeCorners(corners = value.toInt())
        }

//          ____FOR DETECTORS' POSITION CHANGING____
//
//        binding.isDetectorsPositionChangingEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
//            binding.leftDetector.isClickable = isChecked
//            binding.rightDetector.isClickable = isChecked
//
//            if (isChecked) {
//                binding.leftDetector.setOnTouchListener{ view, event ->
//                    changePosition(view, event)
//                    if (event.action == MotionEvent.ACTION_UP) {
//                        view.performClick()
//                    }
//                    true
//                }
//                binding.rightDetector.setOnTouchListener{ view, event ->
//                    changePosition(view, event)
//                    if (event.action == MotionEvent.ACTION_UP) {
//                        view.performClick()
//                    }
//                    true
//                }
//            }
//            else {
//                binding.leftDetector.setOnTouchListener(null)
//                binding.rightDetector.setOnTouchListener(null)
//            }
//
//        }

//        binding.rightDetector.setOnClickListener {
//            Log.d("AFDEBUG", "right clicked")
//        }
//
//        binding.leftDetector.setOnClickListener {
//            Log.d("AFDEBUG", "left clicked")
//        }


        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.overlayConfigs.collectLatest { overlayList ->
                        val right = overlayList.find { it.id == MAIN_OVERLAY_RIGHT }
                        val left = overlayList.find { it.id == MAIN_OVERLAY_LEFT }

                        // val screenWidth = resources.displayMetrics.widthPixels

                        Log.d("AFDEBUG", "returned list: $overlayList")


//                  ____START POSITION CALCULATING____
//
                        right?.let {
//                        val rightDetector = binding.rightDetector
//                        rightDetector.x = (screenWidth - it.x - it.width).toFloat()
//                        rightDetector.y = it.y.toFloat()
//
//                        rightDetector.layoutParams.width = it.width
//                        rightDetector.layoutParams.height = it.height
                        }
//
                        left?.let {
//                        val leftDetector = binding.leftDetector
//                        leftDetector.x = it.x.toFloat()
//                        leftDetector.y = it.y.toFloat()
//
//                        leftDetector.layoutParams.width = it.width
//                        leftDetector.layoutParams.height = it.height
                        }

                    }
                }

                launch {
                    viewModel.previewOverlayStates.collectLatest{ previewStates ->
//                            val targetIds = setOf(
//                                MAIN_OVERLAY_RIGHT,
//                                UP_SWIPE_RIGHT_SIDE_OVERLAY,
//                                LEFT_SWIPE_OVERLAY,
//                                DOWN_SWIPE_RIGHT_SIDE_OVERLAY
//                            )

                        Log.d("AFDEBUG", "returned list2: $previewStates")
//                        pagerAdapter.submitList(previewStates)
                    }
                }
            }
        }

    }

    private fun changeCorners(corners: Int) {}

    private fun changeOpacity(value: Float) {
        TODO("Not yet implemented")
    }

//    private fun changeOpacity(opacity: Float) {
//        val currentPos = binding.previewPager.currentItem
//        val currentItem = pagerAdapter.currentList.getOrNull(currentPos)

//        currentItem?.let {
//            viewModel.updatePreviewOpacity(it.id, opacity)
//        }
//    }

//    private fun changeCorners(corners: Int) {
//        val currentPos = binding.previewPager.currentItem
//        val currentItem = pagerAdapter.currentList.getOrNull(currentPos)
//
//        currentItem?.let {
//            viewModel.updatePreviewCorners(it.id, corners)
//        }
//    }

    // ON TOUCH CHANGING POSITION
    private fun changePosition(view: View?, event: MotionEvent) {
        if (view == null) return

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = view.x - event.rawX
                dY = view.y - event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                view.animate()
                    .x(event.rawX + dX)
                    .y(event.rawY + dY)
                    .setDuration(0)
                    .start()
            }

            MotionEvent.ACTION_UP -> {
                val finalX = view.x.toInt()
                val finalY = view.y.toInt()

//                if (view.id == binding.rightDetector.id) {
//
//                    val screenWidth = resources.displayMetrics.widthPixels
//                    val dbX = screenWidth - finalX - view.width
//                    viewModel.updatePosition(MAIN_OVERLAY_RIGHT, dbX, finalY)
//                } else {
//                    viewModel.updatePosition(MAIN_OVERLAY_LEFT, finalX, finalY)
//                }

            }
        }
    }


//    private fun updateLivePreview(side: String) {
//        val currentPos = binding.previewPager.currentItem
//        val currentItem = pagerAdapter.currentList.getOrNull(currentPos)
//
//        currentItem?.let {
//            viewModel.updatePreviewSide(it.id, side)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// TODO: ObjectAnimator()