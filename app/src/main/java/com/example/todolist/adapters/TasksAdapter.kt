package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.fragments.EditTaskFragment
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
        private const val VIEW_TYPE_AD = 2
    }

    inner class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val adText: TextView = view.findViewById(R.id.ad_text)
        private val closeAdButton: Button = view.findViewById(R.id.ad_close_button)

        fun render(ad: TaskItem.AdEntry) {
            adText.text = ad.text

            closeAdButton.setOnClickListener {
                synchronized(items) {
                    items.removeAt(ad.id)
                    notifyItemRemoved(ad.id)
                }
            }
        }
    }

    fun addAd(ad: TaskItem.AdEntry): Int {
        val i = getRandomIndex()
        ad.id = i
        synchronized(items) {
            items.add(i, ad)
            notifyItemInserted(i)
        }
        return i
    }

    private fun getRandomIndex(): Int {
        val minValue = 0
        val maxValue = items.size - 1
        return (minValue..maxValue).random()
    }

    fun removeAd(i: Int) {
        if (items[i] is TaskItem.AdEntry) {
            synchronized(items) {
                items.removeAt(i)
                notifyItemRemoved(i)
            }
        }
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

                updateTasksList()
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

            VIEW_TYPE_AD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.ad_bar, parent, false)
                AdViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type: $viewType")
        }
    }

    @Synchronized override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TaskItem.TaskEntry -> (holder as TaskViewHolder).render(item)
            is TaskItem.InfoEntry -> (holder as InfoBarViewHolder).render(item)
            is TaskItem.AdEntry -> (holder as AdViewHolder).render(item)
        }
    }

    @Synchronized override fun getItemCount() = items.size

    @Synchronized override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TaskItem.TaskEntry -> VIEW_TYPE_TASK
            is TaskItem.InfoEntry -> VIEW_TYPE_INFO
            is TaskItem.AdEntry -> VIEW_TYPE_AD
        }
    }

    fun updateTasksList() {
        val filters = FilterService.getToggledFilters()

        val newItems: MutableList<TaskItem> = if (showCurrentTasks) {
            TaskService.getFilteredCurrentTasks(filters)
        } else {
            TaskService.getFilteredTasks(filters)
        }

        synchronized(items) {
            items = newItems
        }

        notifyDataSetChanged()
    }
}

sealed class TaskItem {
    data class TaskEntry(val task: Task): TaskItem()
    data class InfoEntry(val message: String) : TaskItem()
    data class AdEntry(var id: Int = -1, val text: String) : TaskItem()
}
