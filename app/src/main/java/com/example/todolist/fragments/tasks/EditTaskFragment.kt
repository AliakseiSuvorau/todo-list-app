package com.example.todolist.fragments.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.fragments.tags.TagsFragment
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.requests.tasks.DeleteTaskRequest
import com.example.todolist.model.requests.tasks.UpdateTaskRequest
import com.example.todolist.model.services.TagService
import com.example.todolist.model.services.TaskService
import com.example.todolist.showFilters
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class EditTaskFragment(
    private val task: Task,
    private val tasksAdapter: TasksAdapter
) : Fragment(R.layout.fragment_edit_task) {

    private var selectedDeadline: Instant? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Task title
        val taskTitle = view.findViewById<EditText>(R.id.task_name)
        taskTitle.setText(task.title)

        // List of tags
        if (showFilters) {
            childFragmentManager.commit {
                replace(R.id.tags_bar, TagsFragment(task.tags))
            }
        }

        // Deadline
        val taskDeadline = view.findViewById<Button>(R.id.task_deadline)
        taskDeadline.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = String.format(Locale.UK, "%04d.%02d.%02d", year, month + 1, day)
                taskDeadline.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }
        if (task.deadline != null) {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
            taskDeadline.text =
                task.deadline!!.atZone(ZoneId.systemDefault()).toLocalDate().format(formatter)
        }

        // Difficulty
        val taskDifficulty = view.findViewById<NumberPicker>(R.id.task_difficulty)
        taskDifficulty.minValue = MIN_DIFFICULTY
        taskDifficulty.maxValue = MAX_DIFFICULTY
        taskDifficulty.value = task.difficulty ?: MIN_DIFFICULTY

        // Description
        val taskDescription = view.findViewById<EditText>(R.id.task_description)
        taskDescription.setText(task.description)

        // "Save" button
        view.findViewById<Button>(R.id.edit_task_save_button).setOnClickListener {
            val request = UpdateTaskRequest(
                taskId = task.taskId,
                title = taskTitle.text.toString(),
                description = taskDescription.text.toString(),
                deadline = selectedDeadline,
                userTags = TagService.getToggledUserTags(),
                difficulty = taskDifficulty.value,
                done = task.done
            )
            TaskService.updateTask(request)

            TagService.clearToggledUserTags()

            tasksAdapter.updateTasksList()

            parentFragmentManager.commit {
                remove(this@EditTaskFragment)
                parentFragmentManager.popBackStack()
            }
        }

        // "Cancel" button
        view.findViewById<Button>(R.id.edit_task_cancel_button).setOnClickListener {
            parentFragmentManager.commit {
                remove(this@EditTaskFragment)
                parentFragmentManager.popBackStack()
            }
        }

        // "Delete" button
        view.findViewById<Button>(R.id.edit_task_delete_button).setOnClickListener {
            showDeleteTaskConfirmationDialog()
        }

        // System "back" button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showDeleteTaskConfirmationDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_task_delete_confirmation, null)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete task")
            .setView(dialogView)
            .setBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.background
                )
            )
            .setPositiveButton("Yes") { _, _ ->
                TaskService.deleteTask(DeleteTaskRequest(task.taskId))

                parentFragmentManager.commit {
                    remove(this@EditTaskFragment)
                    parentFragmentManager.popBackStack()
                }
            }
            .setNegativeButton("No", null)
            .show()
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
