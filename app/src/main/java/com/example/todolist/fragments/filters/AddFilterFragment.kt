package com.example.todolist.fragments.filters

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.todolist.R
import com.example.todolist.adapters.FiltersAdapter
import com.example.todolist.fragments.tags.TagsFragment
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

    private var userFilterName: EditText? = null
    private var checkboxDone: CheckBox? = null
    private var checkboxNotDone: CheckBox? = null
    private var pickDeadlineButton: Button? = null
    private var taskUrgency: EditText? = null

    companion object {
        private const val ADD_FILTER_PREFIX = "add_filter_fragment"
        private const val FILTER_NAME_KEY = "${ADD_FILTER_PREFIX}_filter_name"
        private const val CHECKBOX_DONE_KEY = "${ADD_FILTER_PREFIX}_checkbox_done"
        private const val CHECKBOX_NOT_DONE_KEY = "${ADD_FILTER_PREFIX}_checkbox_not_done"
        private const val DEADLINE_KEY = "${ADD_FILTER_PREFIX}_deadline"
        private const val URGENCY_KEY = "${ADD_FILTER_PREFIX}_urgency"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Filter name
        userFilterName = view.findViewById(R.id.user_filter_name)
        savedInstanceState?.getString(FILTER_NAME_KEY)?.let { userFilterName!!.setText(it) }

        // Tags
        childFragmentManager.commit {
            replace(R.id.tags_bar, TagsFragment())
        }

        // Task completion status
        checkboxDone = view.findViewById(R.id.checkbox_done)
        savedInstanceState?.getBoolean(CHECKBOX_DONE_KEY)?.let { checkboxDone!!.isChecked = it }
        checkboxNotDone = view.findViewById(R.id.checkbox_not_done)
        savedInstanceState?.getBoolean(CHECKBOX_NOT_DONE_KEY)?.let { checkboxNotDone!!.isChecked = it }

        // Deadline
        pickDeadlineButton = view.findViewById(R.id.pick_deadline_button)
        savedInstanceState?.getString(DEADLINE_KEY)?.let { pickDeadlineButton!!.text = it }
        var selectedDeadline: Instant? = null
        pickDeadlineButton!!.setOnClickListener {
            showDatePicker { year, month, day ->
                val selectedDate = String.format(Locale.UK, "%04d.%02d.%02d", year, month+1, day)
                pickDeadlineButton!!.text = selectedDate
                selectedDeadline =
                    LocalDate.of(year, month + 1, day).atStartOfDay().atZone(ZoneId.systemDefault())
                        .toInstant()
            }
        }

        // Urgency
        taskUrgency = view.findViewById(R.id.filter_urgency)
        savedInstanceState?.getInt(URGENCY_KEY)?.let { taskUrgency!!.setText(it) }

        // "Save" button
        view.findViewById<Button>(R.id.add_filter_save_button).setOnClickListener {
            val name = userFilterName!!.text.toString()
            if (name == "") {
                userFilterName!!.error = "This is a required field!"
                return@setOnClickListener
            }

            val deadline = selectedDeadline

            val urgencyString = taskUrgency!!.text.toString()
            var urgencyInt: Int? = null
            if (urgencyString != "") {
                urgencyInt = urgencyString.toInt()
            }
            val done = getDoneBool(checkboxDone!!.isChecked, checkboxNotDone!!.isChecked)

            val request = AddFilterRequest(
                name = name,
                deadline = deadline,
                userTagIds = TagService.getToggledUserTags().map { it.tagId },
                urgency = urgencyInt,
                done = done,
            )

            val newFilter = FilterService.addFilter(request)
            filterAdapter.addNewFilter(newFilter)

            TagService.clearToggledUserTags()

            parentFragmentManager.commit {
                remove(this@AddFilterFragment)
                parentFragmentManager.popBackStack()
            }
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

    private fun getDoneBool(doneTick: Boolean, notDoneTick: Boolean): Boolean? {
        var done: Boolean? = null
        if (doneTick && !notDoneTick) {
            done = true
        }
        if (!doneTick && notDoneTick) {
            done = false
        }
        return done
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        userFilterName?.let { outState.putString(FILTER_NAME_KEY, it.text.toString()) }
        checkboxDone?.let { outState.putBoolean(CHECKBOX_DONE_KEY, it.isChecked) }
        pickDeadlineButton?.let { outState.putString(DEADLINE_KEY, it.text.toString()) }
        taskUrgency?.let {
            val urgencyString = it.text.toString()
            if (urgencyString != "") {
                outState.putInt(URGENCY_KEY, urgencyString.toInt())
            }
        }
    }
}
