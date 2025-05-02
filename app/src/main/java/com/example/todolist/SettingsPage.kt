package com.example.todolist

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

class SettingsPage : Fragment(R.layout.settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialDisableFilter = showFilters
        val initialSortOrder = sortTasksByDifficulty
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

        val sortTasksByDifficultyCheckbox = view.findViewById<CheckBox>(R.id.difficulty_sort_checkbox)
        sortTasksByDifficultyCheckbox.setOnClickListener(null)
        sortTasksByDifficultyCheckbox.isChecked = sortTasksByDifficulty
        sortTasksByDifficultyCheckbox.setOnCheckedChangeListener { _, isChecked ->
            sortTasksByDifficulty = isChecked
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            showFilters = initialDisableFilter
            sortTasksByDifficulty = initialSortOrder
            disableAds = initialDisableAds
            parentFragmentManager.popBackStack()
        }
    }
}