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

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.activities.MainActivity
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.databinding.FragmentMainBinding
import com.example.moreoverlays.utils.LEFT_SIDE
import com.example.moreoverlays.utils.RIGHT_SIDE
import com.example.moreoverlays.viewModels.MainActivityViewModel
import com.google.android.material.button.MaterialButton

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var overlaysRecyclerView: RecyclerView
    private lateinit var rightHandleCV: CardView
    private lateinit var leftHandleCV: CardView
    private lateinit var appearanceBtn: MaterialButton

    private var _overlayList: List<OverlayConfig> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rightHandleCV = binding.cardRightHandle
        leftHandleCV = binding.cardLeftHandle
//        appearanceBtn = binding.btnAppearance

        rightHandleCV.setOnClickListener {
            (activity as? MainActivity)?.openHandleSettingsFragment(RIGHT_SIDE)
        }

        leftHandleCV.setOnClickListener {
            (activity as? MainActivity)?.openHandleSettingsFragment(LEFT_SIDE)
        }

//        appearanceBtn.setOnClickListener {
//            (activity as? MainActivity)?.openAppearanceSettingsFragment()
//        }

        // FOR SERVICE STATUS TRACKING
//        binding.switchMasterToggle.isClickable = false
//        binding.switchMasterToggle.setOnCheckedChangeListener {_, isChecked ->
//            if (isChecked) {
//                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//                startActivity(intent)
//            }
//
//        }
//
//        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        val isServiceRunning = activityManager.runningAppProcesses.any { it.processName == ":overlay_process" }
//
//        binding.switchMasterToggle.isChecked = isServiceRunning

        binding.cardServiceStatus.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            startActivity(intent)
        }

//        overlaysRecyclerView = view.findViewById(R.id.overlaysRecyclerView)
//
//        val prefs = requireContext().getSharedPreferences("global_prefs", MODE_PRIVATE)
//        val jsonString = prefs.getString("overlay_list", null)
//        Log.d("MainFragmentJsonDebugging", "$jsonString")
//        val overlayList = if (jsonString != null) {
//            JsonManager.jsonConfigured.decodeFromString<ArrayList<OverlayConfig>>(jsonString)
//        } else {
//            arrayListOf()
//        }
//        Log.d("MainFragmentJsonDebugging", "$overlayList")
//        overlayList.removeAll { it.id == -1 || it.id == 0 }

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.overlayConfigs.collectLatest { configsList ->
//
//                Log.d("MainFragmentFlow", "Received config list size: ${configsList.size}")
//
//                _overlayList = configsList.toMutableList()
//                    .apply { removeAll { it.id == -1 || it.id == 0 } }
//
//                val currentList = _overlayList.toList()
//
//                if (overlaysRecyclerView.adapter == null) {
//                    overlaysRecyclerView.adapter = RootOverlaysRecyclerViewAdapter(
//                        currentList,
//                        object : RootOverlaysRecyclerViewAdapter.OnItemClickListener {
//                            override fun onItemClick(item: OverlayConfig) {
//                                (activity as? MainActivity)?.openOverlaySettingsFragment(item)
//                            }
//                        }
//                    )
//                    Log.d("MainFragmentFlow", "Adapter created with ${currentList.size} items.")
//                } else {
//
//                    (overlaysRecyclerView.adapter as? RootOverlaysRecyclerViewAdapter)?.updateList(currentList)
//                    Log.d("MainFragmentFlow", "Adapter updated with ${currentList.size} items.")
//                }
//            }
//        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}