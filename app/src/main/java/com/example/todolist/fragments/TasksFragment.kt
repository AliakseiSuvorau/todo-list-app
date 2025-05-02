package com.example.todolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapters.TasksAdapter
import android.view.View
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.adapters.TaskItem
import com.example.todolist.disableAds
import com.example.todolist.model.services.AdService
import com.example.todolist.model.services.TaskService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TasksFragment(
    showCurrentTasks: Boolean = false,
) : Fragment(R.layout.task_list) {

    private lateinit var recyclerTasks: RecyclerView
    private val taskAdapter: TasksAdapter
    private var adJob: Job? = null

    init {
        val tasks = if (showCurrentTasks) {
            TaskService.getAllCurrentTasks()
        } else {
            TaskService.getAllTasks()
        }

        taskAdapter = TasksAdapter(tasks.toMutableList(), showCurrentTasks, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayTaskList(view)

        if (!disableAds) {
            val adService = AdService(taskAdapter) {
                TaskItem.AdEntry (
                    text = "Advertisement"
                )
            }

            adJob = viewLifecycleOwner.lifecycleScope.launch {
                adService.startAdCycle()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adJob?.cancel()
    }

    private fun displayTaskList(view: View) {
        recyclerTasks = view.findViewById(R.id.tasks_recycler)

        recyclerTasks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerTasks.adapter = taskAdapter

        view.findViewById<FloatingActionButton>(R.id.add_task_button).setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragment_container, AddTaskFragment(taskAdapter))
                addToBackStack(null)
            }
        }
    }

    fun getAdapter() = taskAdapter
}
