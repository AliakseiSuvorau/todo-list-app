package com.example.todolist

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapters.FiltersAdapter
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.adapters.UserTagsAdapter
import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.requests.filters.AddFilterRequest
import com.example.todolist.model.requests.tags.AddTagRequest
import com.example.todolist.model.requests.tasks.AddTaskRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TagService
import com.example.todolist.model.services.TaskService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

interface TaskList {
    fun getAllUserFilters(): Iterable<Filter>
    fun getServiceFilters(): Iterable<Filter>
    fun getTasks(filters: Collection<Filter>): Iterable<Task>
}

class AllTasksPage : Fragment(R.layout.fragment_all_tasks), TaskList {
    private val MIN_DIFFICULTY = 1
    private val MAX_DIFFICULTY = 10

    private lateinit var recyclerUserFilters: RecyclerView
    private lateinit var recyclerServiceFilters: RecyclerView
    private lateinit var recyclerUserTags: RecyclerView
    private lateinit var recyclerTasks: RecyclerView

    private lateinit var addUserTagButton: FloatingActionButton

    private lateinit var filterAdapter: FiltersAdapter
    private lateinit var userTagsAdapter: UserTagsAdapter
    private lateinit var taskAdapter: TasksAdapter

    private lateinit var reloader: TaskListReloader

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayUserFilters(view)
        displayServiceFilters(view)
        displayTaskList(view)
        TaskListReloader.init(taskAdapter)
    }

    private fun displayUserFilters(view: View) {
        recyclerUserFilters = view.findViewById(R.id.user_filters_recycler)

        val userFilters = getAllUserFilters()
        recyclerUserFilters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        filterAdapter = FiltersAdapter(userFilters.toMutableList())
        recyclerUserFilters.adapter = filterAdapter

        view.findViewById<FloatingActionButton>(R.id.add_user_filter_button).setOnClickListener {
            showAddFilterDialog()
        }
    }

    private fun displayServiceFilters(view: View) {
        recyclerServiceFilters = view.findViewById(R.id.service_filters_recycler)
        recyclerServiceFilters.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerServiceFilters.adapter =
            FiltersAdapter(getServiceFilters().toMutableList())
    }

    private fun displayTaskList(view: View) {
        recyclerTasks = view.findViewById(R.id.tasks_recycler)

        val tasks = TaskService.getAllTasks()
        recyclerTasks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskAdapter = TasksAdapter(tasks.toMutableList())
        recyclerTasks.adapter = taskAdapter

        view.findViewById<FloatingActionButton>(R.id.add_task_button).setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)

        // Tags
        displayTagsInView(dialogView)

        // Task title
        val taskTitle = dialogView.findViewById<EditText>(R.id.task_name)

        // Task description
        val taskDescription = dialogView.findViewById<EditText>(R.id.task_description)

        // Task deadline
        val taskDeadline = dialogView.findViewById<Button>(R.id.task_deadline)
        var selectedDeadline: Instant? = null
        taskDeadline.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = "$day.${month + 1}.$year"
                taskDeadline.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        // Task difficulty
        val taskDifficulty = dialogView.findViewById<NumberPicker>(R.id.task_difficulty)
        taskDifficulty.minValue = MIN_DIFFICULTY
        taskDifficulty.maxValue = MAX_DIFFICULTY

        // Display dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New task")
            .setView(dialogView)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_all_tasks
                )
            )
            .setPositiveButton("Add") { _, _ ->
                val title = taskTitle.text.toString()
                val description = taskDescription.text.toString()
                val deadline = selectedDeadline
                val difficulty = taskDifficulty.value

                val request = AddTaskRequest(
                    title = title,
                    description = description,
                    deadline = deadline,
                    userTagIds = userTagsAdapter.getToggledTagIds(),
                    difficulty = difficulty,
                )
                val newTask = TaskService.addTask(request)
                taskAdapter.addNewTask(newTask)

                userTagsAdapter.clearToggledTags()

                val toggledFilters = FilterService.getToggledFilters()
                val tasks = TaskService.getFilteredTasks(toggledFilters)
                TaskListReloader.reloadTasksList(tasks)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setUserTags() {
        val userTags = TagService.getUserTags()
        recyclerUserTags.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        userTagsAdapter = UserTagsAdapter(userTags.toMutableList())
        recyclerUserTags.adapter = userTagsAdapter
    }

    override fun getAllUserFilters() = FilterService.getAllUserFilters()
    override fun getServiceFilters() = FilterService.getServiceFilters()
    override fun getTasks(filters: Collection<Filter>) = TaskService.getFilteredTasks(filters)

    private fun showAddFilterDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_filter, null)

        // Filter name
        val userFilterName = dialogView.findViewById<EditText>(R.id.user_filter_name)

        // Tags
        displayTagsInView(dialogView)

        // Task completion status
        val checkboxDone = dialogView.findViewById<CheckBox>(R.id.checkbox_done)

        // Deadline
        val pickDeadlineButton = dialogView.findViewById<Button>(R.id.pick_deadline_button)
        var selectedDeadline: Instant? = null
        pickDeadlineButton.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = "$day.${month + 1}.$year"
                pickDeadlineButton.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        // Difficulty
        val difficultySelector = dialogView.findViewById<NumberPicker>(R.id.difficulty_selector)
        difficultySelector.minValue = MIN_DIFFICULTY
        difficultySelector.maxValue = MAX_DIFFICULTY

        // Display dialog
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New filter")
            .setView(dialogView)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_all_tasks
                )
            )
            .setPositiveButton("Add") { _, _ ->
                val name = userFilterName.text.toString()
                val done = checkboxDone.isChecked
                val deadline = selectedDeadline
                val difficulty = difficultySelector.value

                val request = AddFilterRequest(
                    name = name,
                    deadline = deadline,
                    userTagIds = userTagsAdapter.getToggledTagIds(),
                    difficulty = difficulty,
                    done = done
                )

                val newFilter = FilterService.addFilter(request)
                filterAdapter.addNewFilter(newFilter)

                userTagsAdapter.clearToggledTags()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePicker(onDateSelected: (year: Int, month: Int, day: Int) -> Unit) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                onDateSelected(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun showAddTagDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_tag, null)

        val userTagName = dialogView.findViewById<EditText>(R.id.user_tag_name)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("New tag")
            .setView(dialogView)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background_all_tasks
                )
            )
            .setPositiveButton("Add") { _, _ ->
                val name = userTagName.text.toString()

                val request = AddTagRequest(name = name)

                val newUserTag = TagService.addTag(request)
                userTagsAdapter.addNewTag(newUserTag)

            }
            .setNegativeButton("Back", null)
            .show()
    }

    private fun displayTagsInView(view: View) {
        recyclerUserTags = view.findViewById(R.id.tags_recycler)
        addUserTagButton = view.findViewById(R.id.add_user_tag_button)

        addUserTagButton.setOnClickListener {
            showAddTagDialog()
        }

        setUserTags()
    }
}
