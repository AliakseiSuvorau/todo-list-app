package com.example.todolist.model.services

import com.example.todolist.adapters.TaskItem
import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.repositories.TaskRepository
import com.example.todolist.model.requests.tasks.AddTaskRequest
import com.example.todolist.model.requests.tasks.DeleteTaskRequest
import com.example.todolist.model.requests.tasks.UpdateTaskRequest
import java.time.Instant
import java.time.ZoneId

object TaskService {
    private const val EMPTY_TASK_LIST_MSG = "No tasks!"
    private const val TASKS_FOR_TODAY_MSG = "Tasks for today"
    private const val TASKS_FOR_TOMORROW_MSG = "Tasks for tomorrow"

    fun addTask(request: AddTaskRequest): Task {
        val tags = TagRepository.getMultipleByIds(request.userTagIds)

        val newTask = Task(
            title = request.title,
            description = request.description,
            deadline = request.deadline,
            difficulty = request.difficulty,
            done = request.done,
            tags = tags,
        )

        val newTaskId = TaskRepository.upsert(newTask)
        for (tagId in request.userTagIds) {
            TaskRepository.linkTag(tagId, newTaskId)
        }

        newTask.taskId = newTaskId
        return newTask
    }

    private fun convertTasksToTaskEntries(tasks: Iterable<Task>) =
        tasks.map { TaskItem.TaskEntry(it) }

    fun getFilteredTasks(filters: Collection<Filter>, showTasksWithoutDeadline: Boolean = true): MutableList<TaskItem> {
        var tasks = TaskRepository.getAll()

        if (filters.isEmpty()) {
            return convertTasksToTaskEntries(tasks).toMutableList()
        }

        for (filter in filters) {
            tasks = FilterService.filterTasks(tasks, filter, showTasksWithoutDeadline).toMutableList()
        }

        if (tasks.isEmpty()) {
            return mutableListOf(getEmptyTaskListMessageBar())
        }

        return convertTasksToTaskEntries(tasks).toMutableList()
    }

    private fun getFilteredTasksForToday(filters: Iterable<Filter>): MutableList<TaskItem> {
        val items = mutableListOf(getTasksForTodayMessageBar())
        val allFilters = mutableListOf(
            Filter(
                name = "Today",
                tags = mutableListOf(
                    TagService.getDeadlineTag(
                        Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(1)
                            .atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant()
                    ),
                    TagService.getCompletionTag(false)
                )
            )
        )
        allFilters.addAll(filters)

        val tasksForToday = getFilteredTasks(allFilters, showTasksWithoutDeadline = false)

        items.addAll(tasksForToday)
        return items
    }

    private fun getFilteredTasksForTomorrow(filters: Iterable<Filter>): MutableList<TaskItem> {
        val items = mutableListOf(getTasksForTomorrowMessageBar())
        val allFilters = mutableListOf(
            Filter(
                name = "Tomorrow",
                tags = mutableListOf(
                    TagService.getDeadlineTag(
                        Instant.now().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(2)
                            .atStartOfDay(ZoneId.systemDefault()).minusNanos(1).toInstant()
                    ),
                    TagService.getCompletionTag(false)
                )
            )
        )
        allFilters.addAll(filters)

        val tasksForTomorrow = getFilteredTasks(allFilters, showTasksWithoutDeadline = false)

        items.addAll(tasksForTomorrow)
        return items
    }

    fun updateTask(request: UpdateTaskRequest) {
        val task = Task(
            taskId = request.taskId,
            title = request.title,
            description = request.description,
            deadline = request.deadline,
            difficulty = request.difficulty,
            tags = request.userTags,
            done = request.done
        )

        TaskRepository.upsert(task)
        for (tag in task.tags) {
            TaskRepository.linkTag(tag.tagId, task.taskId)
        }
    }

    fun deleteTask(request: DeleteTaskRequest) {
        TaskRepository.delete(request.taskId)
        TaskRepository.unlinkTags(request.taskId)
    }

    fun checkTask(task: Task, filters: Iterable<Filter>) =
        filters.all { filter -> FilterService.checkTask(task, filter) }

    fun getAllTasks() = getFilteredTasks(emptyList())
    fun getNumOfTasks() = getAllTasks().size

    private fun getEmptyTaskListMessageBar(): TaskItem {
        return TaskItem.InfoEntry(
            message = EMPTY_TASK_LIST_MSG
        )
    }

    private fun getTasksForTodayMessageBar(): TaskItem {
        return TaskItem.InfoEntry(
            message = TASKS_FOR_TODAY_MSG
        )
    }

    private fun getTasksForTomorrowMessageBar(): TaskItem {
        return TaskItem.InfoEntry(
            message = TASKS_FOR_TOMORROW_MSG
        )
    }

    fun getFilteredCurrentTasks(filters: Collection<Filter>): MutableList<TaskItem> {
        val currentTasks = getFilteredTasksForToday(filters)
        currentTasks.addAll(getFilteredTasksForTomorrow(filters))
        return currentTasks
    }

    fun getAllCurrentTasks() = getFilteredCurrentTasks(mutableListOf())
}
