package com.example.moreoverlays.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.viewModels.MainActivityViewModel
import com.example.moreoverlays.activities.MainActivity
import com.example.moreoverlays.adapters.RootOverlaysRecyclerViewAdapter
import com.example.moreoverlays.database.OverlayConfig
import com.example.moreoverlays.databinding.FragmentMainBinding
import com.example.moreoverlays.utils.LEFT_SIDE
import com.example.moreoverlays.utils.RIGHT_SIDE
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var overlaysRecyclerView: RecyclerView
    private lateinit var rightHandleCV: CardView
    private lateinit var leftHandleCV: CardView

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

        rightHandleCV.setOnClickListener {
            (activity as? MainActivity)?.openHandleSettingsFragment(RIGHT_SIDE)
        }

        leftHandleCV.setOnClickListener {
            (activity as? MainActivity)?.openHandleSettingsFragment(LEFT_SIDE)
        }




//        overlaysRecyclerView = view.findViewById(R.id.overlaysRecyclerView)
//        // TODO: CHANGE DATA GETTING FROM THIS TO DB
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