package com.example.todolist.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.todolist.R
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.model.requests.tasks.AddTaskRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TagService
import com.example.todolist.model.services.TaskService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class AddTaskDialogFragment(
    private val tasksAdapter: TasksAdapter
) : DialogFragment() {

    private var selectedDeadline: Instant? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_task, null)

        val taskTitle = dialogView.findViewById<EditText>(R.id.task_name)
        val taskDescription = dialogView.findViewById<EditText>(R.id.task_description)
        val taskDeadline = dialogView.findViewById<Button>(R.id.task_deadline)
        val taskDifficulty = dialogView.findViewById<NumberPicker>(R.id.task_difficulty)

        taskDeadline.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = "$day.${month + 1}.$year"
                taskDeadline.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        taskDifficulty.minValue = MIN_DIFFICULTY
        taskDifficulty.maxValue = MAX_DIFFICULTY

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("New task")
            .setView(dialogView)
            .setBackground(ContextCompat.getDrawable(requireContext(),
                R.drawable.background_all_tasks
            ))
            .setPositiveButton("Add") { _, _ ->
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
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    override fun onStart() {
        super.onStart()

        val existing = childFragmentManager.findFragmentById(R.id.tags_bar)
        if (existing == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.tags_bar, TagsFragment())
                .commitNow()
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
