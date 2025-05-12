package com.example.todolist.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R
import com.example.todolist.fragments.tasks.TasksFragment
import com.example.todolist.fragments.filters.UserFiltersFragment
import com.example.todolist.showFilters

class CurrentTasksFragment : Fragment(R.layout.fragment_current_tasks) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasksFragment = TasksFragment(showCurrentTasks = true)

        if (showFilters) {
            childFragmentManager.commit {
                replace(R.id.task_list, tasksFragment)
                replace(R.id.user_filters_list, UserFiltersFragment(tasksFragment.getAdapter()))
                addToBackStack(null)
            }
        } else {
            childFragmentManager.commit {
                replace(R.id.task_list, tasksFragment)
                addToBackStack(null)
            }
        }
    }
}
