package com.example.todolist.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.model.requests.tasks.AddTaskRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TagService
import com.example.todolist.model.services.TaskService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class AddTaskFragment(
    private val tasksAdapter: TasksAdapter
) : Fragment(R.layout.fragment_add_task) {

    private var selectedDeadline: Instant? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Task title
        val taskTitle = view.findViewById<EditText>(R.id.task_name)

        // List of tags
        childFragmentManager.commit {
            replace(R.id.tags_bar, TagsFragment())
        }

        // Deadline
        val taskDeadline = view.findViewById<Button>(R.id.task_deadline)
        taskDeadline.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = "$day.${month + 1}.$year"
                taskDeadline.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        // Difficulty
        val taskDifficulty = view.findViewById<NumberPicker>(R.id.task_difficulty)
        taskDifficulty.minValue = MIN_DIFFICULTY
        taskDifficulty.maxValue = MAX_DIFFICULTY

        // Description
        val taskDescription = view.findViewById<EditText>(R.id.task_description)

        // "Save" button
        view.findViewById<Button>(R.id.add_task_save_button).setOnClickListener {
            val request = AddTaskRequest(
                title = taskTitle.text.toString(),
                description = taskDescription.text.toString(),
                deadline = selectedDeadline,
                userTagIds = TagService.getToggledUserTags().map { it.tagId },
                difficulty = taskDifficulty.value
            )
            TaskService.addTask(request)

            TagService.clearToggledUserTags()


            val toggledFilters = FilterService.getToggledFilters()
            tasksAdapter.updateTasksList(toggledFilters)

            parentFragmentManager.commit {
                remove(this@AddTaskFragment)
                parentFragmentManager.popBackStack()
            }
        }

        // "Cancel" button
        view.findViewById<Button>(R.id.add_task_cancel_button).setOnClickListener {
            parentFragmentManager.commit {
                remove(this@AddTaskFragment)
                parentFragmentManager.popBackStack()
            }
        }

        // System "back" button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDatePicker(onDateSelected: (Int, Int, Int) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day -> onDateSelected(year, month, day) },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    companion object {
        private const val MIN_DIFFICULTY = 1
        private const val MAX_DIFFICULTY = 10
    }
}
