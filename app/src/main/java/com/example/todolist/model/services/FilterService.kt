package com.example.todolist.model.services

import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.repositories.FilterRepository
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.requests.filters.AddFilterRequest

object FilterService {
    private val toggledFilters = mutableListOf<Filter>()

    fun addFilter(request: AddFilterRequest): Filter {
        val tags = TagRepository.getMultipleByIds(request.userTagIds)
        if (request.deadline != null) {
            tags.add(TagService.addDeadlineTag(request.deadline))
        }
        if (request.difficulty != null) {
            tags.add(TagService.addDifficultyTag(request.difficulty))
        }
        if (request.done != null) {
            tags.add(TagService.addCompletionTag(request.done))
        }

        val filter = Filter(
            name = request.name,
            tags = tags,
        )

        val newFilterId = FilterRepository.upsert(filter)
        val tagIds = tags.map { it.tagId }
        for (userTagId in tagIds) {
            FilterRepository.linkTag(userTagId, newFilterId)
        }

        filter.filterId = newFilterId

        return filter
    }

    fun getAllUserFilters() = FilterRepository.getAll()
    fun getServiceFilters(): Iterable<Filter> {
        return listOf(
            Filter(
                name = "Planned",
                tags = mutableListOf(
                    TagService.getCompletionTag(done = false)
                )
            ),
            Filter(
                name = "Completed",
                tags = mutableListOf(
                    TagService.getCompletionTag(done = true)
                )
            )
        )
    }

    fun filterTasks(tasks: Iterable<Task>, filter: Filter, showTasksWithoutDeadline: Boolean = true) = tasks.filter { task -> checkTask(task, filter, showTasksWithoutDeadline) }
    fun checkTask(task: Task, filter: Filter, showTasksWithoutDeadline: Boolean = true) = filter.tags.all { filterTag -> TagService.checkTag(task, filterTag, showTasksWithoutDeadline) }

    fun addFilterToToggled(filter: Filter) {
        if (!toggledFilters.contains(filter)) {
            toggledFilters.add(filter)
        }
    }

    fun removeFilterFromToggled(filter: Filter) {
        toggledFilters.remove(filter)
    }

    fun getToggledFilters() = toggledFilters
}