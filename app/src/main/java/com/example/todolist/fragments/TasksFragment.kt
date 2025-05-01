package com.example.todolist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapters.TasksAdapter
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.R
import com.example.todolist.TaskListReloader
import com.example.todolist.model.services.TaskService
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TasksFragment : Fragment(R.layout.task_list) {

    private lateinit var recyclerTasks: RecyclerView
    private lateinit var taskAdapter: TasksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayTaskList(view)
        TaskListReloader.init(taskAdapter)
    }

    private fun displayTaskList(view: View) {
        recyclerTasks = view.findViewById(R.id.tasks_recycler)

        val tasks = TaskService.getAllTasks()
        recyclerTasks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskAdapter = TasksAdapter(tasks.toMutableList())
        recyclerTasks.adapter = taskAdapter

        view.findViewById<FloatingActionButton>(R.id.add_task_button).setOnClickListener {
            AddTaskDialogFragment().show(childFragmentManager, "AddTaskDialog")
        }
    }
}
