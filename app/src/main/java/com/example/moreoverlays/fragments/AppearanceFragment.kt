package com.example.moreoverlays.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.moreoverlays.R
import com.example.moreoverlays.adapters.PreviewAdapter
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.databinding.FragmentAppearanceBinding
import com.example.moreoverlays.utils.DOWN_SWIPE_RIGHT_SIDE_OVERLAY
import com.example.moreoverlays.utils.LEFT_SWIPE_OVERLAY
import com.example.moreoverlays.utils.MAIN_OVERLAY_LEFT
import com.example.moreoverlays.utils.MAIN_OVERLAY_RIGHT
import com.example.moreoverlays.utils.UP_SWIPE_RIGHT_SIDE_OVERLAY
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

    private lateinit var pagerAdapter: PreviewAdapter
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

        val pager = binding.previewPager
        pagerAdapter = PreviewAdapter()
        pager.adapter = pagerAdapter

        val currentItem = pagerAdapter.currentList.getOrNull(pager.currentItem)



        binding.toggleSideSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val side = when (checkedId) {
                    R.id.btnLeft -> "left"
                    R.id.btnBoth -> "both"
                    else -> "right"
                }
                updateLivePreview(side)
            }
        }

        binding.previewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val item = pagerAdapter.currentList.getOrNull(position)
                item?.let {
                    val buttonId = when(it.displayMode) {
                        "left" -> R.id.btnLeft
                        "both" -> R.id.btnBoth
                        else -> R.id.btnRight
                    }
                    currentOverlayIds = item.overlayIds

                    binding.toggleSideSelector.check(buttonId)
                }
            }
        })

        binding.sliderOpacity.setLabelFormatter { value ->
            if (value % 1 == 0f) {
                value.toInt().toString()
            } else {
                "%.1f".format(value).replace(",", ".")
            }
        }

        binding.sliderWidth.setLabelFormatter { value ->
            "${value.toInt()}%"
        }

//        binding.leftDetector.alpha = binding.sliderOpacity.value
//        binding.rightDetector.alpha = binding.sliderOpacity.value

        binding.sliderOpacity.addOnChangeListener {slider, value, fromUser ->
            changeOpacity(value)
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
                        pagerAdapter.submitList(previewStates)
                    }
                }
            }
        }

    }

    private fun changeOpacity(opacity: Float) {
        val currentPos = binding.previewPager.currentItem
        val currentItem = pagerAdapter.currentList.getOrNull(currentPos)

        currentItem?.let {
            viewModel.updatePreviewOpacity(it.id, opacity)
        }
    }

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


    private fun updateLivePreview(side: String) {
        val currentPos = binding.previewPager.currentItem
        val currentItem = pagerAdapter.currentList.getOrNull(currentPos)

        currentItem?.let {
            viewModel.updatePreviewSide(it.id, side)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// TODO: ObjectAnimator()