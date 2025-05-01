package com.example.todolist.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R
import com.example.todolist.adapters.FiltersAdapter
import com.example.todolist.model.requests.filters.AddFilterRequest
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TagService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale

class AddFilterFragment(
    private val filterAdapter: FiltersAdapter
) : Fragment(R.layout.fragment_add_filter) {

    companion object {
        private const val MIN_DIFFICULTY = 1
        private const val MAX_DIFFICULTY = 10
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Filter name
        val userFilterName = view.findViewById<EditText>(R.id.user_filter_name)

        // Tags
        childFragmentManager.commit {
            replace(R.id.tags_bar, TagsFragment())
        }

        // Task completion status
        val checkboxDone = view.findViewById<CheckBox>(R.id.checkbox_done)

        // Deadline
        val pickDeadlineButton = view.findViewById<Button>(R.id.pick_deadline_button)
        var selectedDeadline: Instant? = null
        pickDeadlineButton.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = String.format(Locale.UK, "%04d.%02d.%02d", year, month+1, day)
                pickDeadlineButton.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        // Difficulty
        val difficultySelector = view.findViewById<NumberPicker>(R.id.difficulty_selector)
        difficultySelector.minValue = MIN_DIFFICULTY
        difficultySelector.maxValue = MAX_DIFFICULTY

        // "Save" button
        view.findViewById<Button>(R.id.add_filter_save_button).setOnClickListener {
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

        // "Cancel" button
        view.findViewById<Button>(R.id.add_filter_cancel_button).setOnClickListener {
            parentFragmentManager.commit {
                remove(this@AddFilterFragment)
                parentFragmentManager.popBackStack()
            }
        }

        // System "Back" button
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
}