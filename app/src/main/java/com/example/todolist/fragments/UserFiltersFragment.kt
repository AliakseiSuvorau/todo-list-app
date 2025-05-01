package com.example.todolist.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.adapters.FiltersAdapter
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.model.services.FilterService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class UserFiltersFragment(
    private val tasksAdapter: TasksAdapter,
) : Fragment(R.layout.user_filter_list) {

    private lateinit var recyclerUserFilters: RecyclerView
    private lateinit var filterAdapter: FiltersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerUserFilters = view.findViewById(R.id.user_filters_recycler)

        val userFilters = FilterService.getAllUserFilters()
        recyclerUserFilters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        filterAdapter = FiltersAdapter(userFilters.toMutableList(), tasksAdapter)
        recyclerUserFilters.adapter = filterAdapter

        view.findViewById<FloatingActionButton>(R.id.add_user_filter_button).setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragment_container, AddFilterFragment(filterAdapter))
                addToBackStack(null)
            }
        }
    }
}