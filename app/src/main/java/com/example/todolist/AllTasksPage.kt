package com.example.todolist

import androidx.fragment.app.Fragment
import com.example.todolist.filters.Filter
import com.example.todolist.filters.Tag
import com.example.todolist.tasks.Task

interface AllTasksPage {
    fun getAllTags(): Iterable<Tag>
    fun getServiceTags(): Iterable<Tag>
    fun getFilters(): Iterable<Filter>
    fun getTasks(f: Filter): Iterable<Task>
}

class AllTasksList : Fragment(R.layout.fragment_all_tasks), AllTasksPage {
    override fun getAllTags(): Iterable<Tag> {
        TODO("Not yet implemented")
    }

    override fun getServiceTags(): Iterable<Tag> {
        TODO("Not yet implemented")
    }

    override fun getFilters(): Iterable<Filter> {
        TODO("Not yet implemented")
    }

    override fun getTasks(f: Filter): Iterable<Task> {
        TODO("Not yet implemented")
    }
}
