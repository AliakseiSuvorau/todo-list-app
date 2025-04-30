package com.example.todolist

import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.model.dtos.tasks.Task

object TaskListReloader {

    private lateinit var taskAdapter: TasksAdapter

    fun init(taskAdapter: TasksAdapter) {
        this.taskAdapter = taskAdapter
    }

    fun reloadTasksList(tasks: MutableCollection<Task>) {
        taskAdapter.setTasksList(tasks.toMutableList())
        taskAdapter.notifyDataSetChanged()
    }
}
