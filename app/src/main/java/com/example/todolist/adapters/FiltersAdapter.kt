package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.TaskListReloader
import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.services.FilterService
import com.example.todolist.model.services.TaskService

class FiltersAdapter(
    private val filters: MutableList<Filter>,
) : RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button = view.findViewById<ToggleButton>(R.id.user_filter_button)

        fun render(filter: Filter) {
            button.text = filter.name
            button.textOn = filter.name
            button.textOff = filter.name

            button.setOnCheckedChangeListener(null)

            updateButtonBackground(button.isChecked)

            button.setOnCheckedChangeListener { _, isChecked ->
                updateButtonBackground(isChecked)
                handleFilterToggle(filter, isChecked)
            }
        }

        private fun handleFilterToggle(filter: Filter, isChecked: Boolean) {
            if (isChecked) {
                FilterService.addFilterToToggled(filter)
            } else {
                FilterService.removeFilterFromToggled(filter)
            }

            val toggledFilters = FilterService.getToggledFilters()
            val tasks = TaskService.getFilteredTasks(toggledFilters)
            TaskListReloader.reloadTasksList(tasks)
        }

        private fun updateButtonBackground(isChecked: Boolean) {
            if (isChecked) {
                button.setBackgroundResource(R.drawable.toggle_button_background_on)
            } else {
                button.setBackgroundResource(R.drawable.toggle_button_background_off)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) = holder.render(filters[position])
    override fun getItemCount() = filters.size

    fun addNewFilter(filter: Filter) {
        filters.add(filter)
        notifyItemInserted(filters.size - 1)
    }
}
