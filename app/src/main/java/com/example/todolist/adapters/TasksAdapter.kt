package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.TaskListReloader
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.requests.tasks.UpdateTaskRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TaskService

class TasksAdapter(    
    private var tasks: MutableList<Task>
): RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view.findViewById<TextView>(R.id.task_name)
        private var checkBox = view.findViewById<CheckBox>(R.id.task_done)

        fun render(task: Task) {
            textView.text = task.title

            checkBox.setOnCheckedChangeListener(null)

            checkBox.isChecked = task.done

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                val request = UpdateTaskRequest(
                    taskId = task.taskId,
                    title = task.title,
                    description = task.description,
                    deadline = task.deadline,
                    difficulty = task.difficulty,
                    done = isChecked
                )

                TaskService.updateTask(request)

                val toggledFilters = FilterService.getToggledFilters()
                val tasks = TaskService.getFilteredTasks(toggledFilters)
                TaskListReloader.reloadTasksList(tasks)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_bar, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) = holder.render(tasks[position])
    override fun getItemCount() = tasks.size

    fun addNewTask(task: Task) {
        tasks.add(task)
        notifyItemInserted(tasks.size - 1)
    }

    fun setTasksList(tasks: MutableList<Task>) {
        this.tasks = tasks
    }
}