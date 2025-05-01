package com.example.todolist.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.todolist.R
import com.example.todolist.adapters.FiltersAdapter
import com.example.todolist.model.requests.filters.AddFilterRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TagService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class AddFilterDialogFragment(
    private val filterAdapter: FiltersAdapter
) : DialogFragment() {

    private val MIN_DIFFICULTY = 1
    private val MAX_DIFFICULTY = 10

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_filter, null)

        // Filter name
        val userFilterName = dialogView.findViewById<EditText>(R.id.user_filter_name)

        // Tags
        childFragmentManager.beginTransaction()
            .replace(R.id.tags_bar, TagsFragment())
            .commit()

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
        return MaterialAlertDialogBuilder(requireContext())
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
                    userTagIds = TagService.getToggledUserTags().map { it.tagId },
                    difficulty = difficulty,
                    done = done
                )

                val newFilter = FilterService.addFilter(request)
                filterAdapter.addNewFilter(newFilter)

                TagService.clearToggledUserTags()
            }
            .setNegativeButton("Cancel", null)
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
}