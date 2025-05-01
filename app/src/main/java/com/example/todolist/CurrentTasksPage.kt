package com.example.todolist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.fragments.TasksFragment
import com.example.todolist.fragments.UserFiltersFragment

class CurrentTasksPage : Fragment(R.layout.fragment_current_tasks) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasksFragment = TasksFragment(showCurrentTasks = true)
        childFragmentManager.commit {
            replace(R.id.task_list, tasksFragment)
            replace(R.id.user_filters_list, UserFiltersFragment(tasksFragment.getAdapter()))
            addToBackStack(null)
        }
    }
}
