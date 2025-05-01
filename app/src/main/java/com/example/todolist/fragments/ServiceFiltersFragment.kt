package com.example.todolist.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapters.FiltersAdapter
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.model.services.FilterService

class ServiceFiltersFragment(
    private val tasksAdapter: TasksAdapter,
) : Fragment(R.layout.service_filter_list) {

    private lateinit var recyclerServiceFilters: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerServiceFilters = view.findViewById(R.id.service_filters_recycler)
        recyclerServiceFilters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerServiceFilters.adapter =
            FiltersAdapter(getServiceFilters().toMutableList(), tasksAdapter)
    }

    private fun getServiceFilters() = FilterService.getServiceFilters()
}