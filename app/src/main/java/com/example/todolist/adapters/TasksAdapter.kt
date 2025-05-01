package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.fragments.EditTaskFragment
import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.requests.tasks.UpdateTaskRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TaskService

class TasksAdapter(
    private var items: MutableList<TaskItem>,
    private val showCurrentTasks: Boolean = false,
    private val fragment: Fragment,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_TASK = 0
        private const val VIEW_TYPE_INFO = 1
    }

    class InfoBarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val infoText: TextView = view.findViewById(R.id.info_text)

        fun render(info: TaskItem.InfoEntry) {
            infoText.text = info.message
        }
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view.findViewById<TextView>(R.id.task_name)
        private var checkBox = view.findViewById<CheckBox>(R.id.task_done)
        private val taskBar = view.findViewById<LinearLayout>(R.id.task_bar)

        fun render(entry: TaskItem.TaskEntry) {
            val task = entry.task

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
                updateTasksList(toggledFilters)
            }

            taskBar.setOnClickListener {
                fragment.requireActivity().supportFragmentManager.commit {
                    replace(R.id.fragment_container, EditTaskFragment(task, this@TasksAdapter))
                    addToBackStack(null)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TASK -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.task_bar, parent, false)
                TaskViewHolder(view)
            }

            VIEW_TYPE_INFO -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.info_bar, parent, false)
                InfoBarViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TaskItem.TaskEntry -> (holder as TaskViewHolder).render(item)
            is TaskItem.InfoEntry -> (holder as InfoBarViewHolder).render(item)
        }
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TaskItem.TaskEntry -> VIEW_TYPE_TASK
            is TaskItem.InfoEntry -> VIEW_TYPE_INFO
        }
    }

    fun updateTasksList(filters: Collection<Filter>) {
        if (showCurrentTasks) {
            this.items = TaskService.getFilteredCurrentTasks(filters)
        } else {
            this.items = TaskService.getFilteredTasks(filters)
        }
        notifyDataSetChanged()
    }
}

sealed class TaskItem {
    data class TaskEntry(val task: Task): TaskItem()
    data class InfoEntry(val message: String) : TaskItem()
}
