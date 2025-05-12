package com.example.todolist.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.todolist.R
import com.example.todolist.disableAds
import com.example.todolist.showFilters
import com.example.todolist.sortTasksByUrgency

class SettingsFragment : Fragment(R.layout.settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialDisableFilter = showFilters
        val initialSortOrder = sortTasksByUrgency
        val initialDisableAds = disableAds

        val disableFiltersCheckbox = view.findViewById<CheckBox>(R.id.disable_filters_checkbox)
        disableFiltersCheckbox.setOnCheckedChangeListener(null)
        disableFiltersCheckbox.isChecked = !showFilters
        disableFiltersCheckbox.setOnCheckedChangeListener { _, isChecked ->
            showFilters = !isChecked
        }

        val disableAdsCheckbox = view.findViewById<CheckBox>(R.id.disable_ads_checkbox)
        disableAdsCheckbox.setOnClickListener(null)
        disableAdsCheckbox.isChecked = disableAds
        disableAdsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            disableAds = isChecked
        }

        val sortTasksByUrgencyCheckbox = view.findViewById<CheckBox>(R.id.urgency_sort_checkbox)
        sortTasksByUrgencyCheckbox.setOnClickListener(null)
        sortTasksByUrgencyCheckbox.isChecked = sortTasksByUrgency
        sortTasksByUrgencyCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sortTasksByUrgency = isChecked
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            showFilters = initialDisableFilter
            sortTasksByUrgency = initialSortOrder
            disableAds = initialDisableAds
            parentFragmentManager.popBackStack()
        }
    }
}
