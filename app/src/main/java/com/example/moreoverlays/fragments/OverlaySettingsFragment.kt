package com.example.moreoverlays.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.moreoverlays.R
import com.example.moreoverlays.adapters.ViewsOnOverlayRecyclerViewAdapter
import com.example.moreoverlays.database.OverlayConfig


class OverlaySettingsFragment : Fragment(R.layout.fragment_overlay_settings) {

    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val overlay = arguments?.getParcelable<OverlayConfig>("overlay_item")

        recyclerView = view.findViewById(R.id.recyclerView)

        if (overlay != null) {
            recyclerView.adapter = ViewsOnOverlayRecyclerViewAdapter(overlay.contentTypes)
        }
    }






}