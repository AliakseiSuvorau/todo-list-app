package com.example.todolist.fragments.tasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R
import com.example.todolist.adapters.TasksAdapter
import com.example.todolist.fragments.tags.TagsFragment
import com.example.todolist.model.requests.tasks.AddTaskRequest
import com.example.todolist.model.services.TagService
import com.example.todolist.model.services.TaskService
import com.example.todolist.showFilters
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

class AddTaskFragment(
    private val tasksAdapter: TasksAdapter
) : Fragment(R.layout.fragment_add_task) {

    private var selectedDeadline: Instant? = null
    private var taskTitle: EditText? = null
    private var taskDeadline: Button? = null
    private var taskUrgency: EditText? = null
    private var taskDescription: EditText? = null

    companion object {
        private const val ADD_TASK_PREFIX = "add_task_fragment"
        private const val TASK_TITLE_KEY = "${ADD_TASK_PREFIX}_task_title"
        private const val DEADLINE_KEY = "${ADD_TASK_PREFIX}_deadline"
        private const val URGENCY_KEY = "${ADD_TASK_PREFIX}_urgency"
        private const val DESCRIPTION_KEY = "${ADD_TASK_PREFIX}_description"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Task title
        taskTitle = view.findViewById(R.id.task_name)
        savedInstanceState?.getString(TASK_TITLE_KEY)?.let { taskTitle!!.setText(it) }

        // List of tags
        if (showFilters) {
            childFragmentManager.commit {
                replace(R.id.tags_bar, TagsFragment())
            }
        }

        // Deadline
        taskDeadline = view.findViewById(R.id.task_deadline)
        savedInstanceState?.getString(DEADLINE_KEY)?.let { taskDeadline!!.text = it }
        taskDeadline!!.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = String.format(Locale.UK, "%04d.%02d.%02d", year, month + 1, day)
                taskDeadline!!.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        // Urgency
        taskUrgency = view.findViewById(R.id.task_urgency)
        savedInstanceState?.getInt(URGENCY_KEY)?.let { taskUrgency!!.setText(it) }

        // Description
        taskDescription = view.findViewById(R.id.task_description)
        savedInstanceState?.getString(DESCRIPTION_KEY)?.let { taskDescription!!.setText(it) }

        // "Save" button
        view.findViewById<Button>(R.id.add_task_save_button).setOnClickListener {
            val title = taskTitle!!.text.toString()
            if (title == "") {
                taskTitle!!.error = "This is a required field!"
                return@setOnClickListener
            }

            val urgencyString = taskUrgency!!.text.toString()
            var urgencyInt: Int? = null
            if (urgencyString != "") {
                urgencyInt = urgencyString.toInt()
            }

            val request = AddTaskRequest(
                title = title,
                description = taskDescription!!.text.toString(),
                deadline = selectedDeadline,
                userTagIds = TagService.getToggledUserTags().map { it.tagId },
                urgency = urgencyInt
            )
            TaskService.addTask(request)

            TagService.clearToggledUserTags()

            tasksAdapter.updateTasksList()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        taskTitle?.let { outState.putString(TASK_TITLE_KEY, it.text.toString()) }
        taskDeadline?.let { outState.putString(DEADLINE_KEY, it.text.toString()) }
        taskUrgency?.let {
            val urgencyString = it.text.toString()
            if (urgencyString != "") {
                outState.putInt(URGENCY_KEY, urgencyString.toInt())
            }
        }
        taskDescription?.let { outState.putString(DESCRIPTION_KEY, it.text.toString()) }
    }
}
