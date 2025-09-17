package com.example.moreoverlays.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.activities.MainActivity
import com.example.moreoverlays.adapters.RootOverlaysRecyclerViewAdapter
import com.example.moreoverlays.database.OverlayConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var overlaysRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: CHANGE DATA GETTING FROM THIS TO DB
        val prefs = requireContext().getSharedPreferences("global_prefs", MODE_PRIVATE)
        val json = prefs.getString("overlay_list", null)

        val type = object : TypeToken<ArrayList<OverlayConfig>>() {}.type
        val overlayList: ArrayList<OverlayConfig> = Gson().fromJson(json, type)
        overlayList.removeAll { it.id == -1 || it.id == 0 }


        overlaysRecyclerView = view.findViewById(R.id.overlaysRecyclerView)
        overlaysRecyclerView.adapter = RootOverlaysRecyclerViewAdapter(overlayList, object:
        RootOverlaysRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(item: OverlayConfig) {
                (activity as? MainActivity)?.openOverlaySettingsFragment(item)
            }
        })

    }


}