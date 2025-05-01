package com.example.todolist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.fragments.ServiceFiltersFragment
import com.example.todolist.fragments.TasksFragment
import com.example.todolist.fragments.UserFiltersFragment

class AllTasksPage : Fragment(R.layout.fragment_all_tasks) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasksFragment = TasksFragment(showCurrentTasks = false)
        childFragmentManager.commit {
            replace(R.id.task_list, tasksFragment)
            replace(R.id.user_filters_list, UserFiltersFragment(tasksFragment.getAdapter()))
            replace(R.id.service_filters_list, ServiceFiltersFragment(tasksFragment.getAdapter()))
            addToBackStack(null)
        }
    }
}
