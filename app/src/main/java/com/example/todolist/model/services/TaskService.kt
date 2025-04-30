package com.example.todolist.model.services

import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.repositories.TaskRepository
import com.example.todolist.model.requests.tasks.AddTaskRequest
import com.example.todolist.model.requests.tasks.UpdateTaskRequest

object TaskService {
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

    fun getFilteredTasks(filters: Collection<Filter>) : MutableList<Task> {
        var tasks = TaskRepository.getAll()

        if (filters.isEmpty()) {
            return tasks.toMutableList()
        }

        for (filter in filters) {
            tasks = FilterService.filterTasks(tasks, filter).toMutableList()
        }

        return tasks.toMutableList()
    }

    fun updateTask(request: UpdateTaskRequest) {
        val task = Task (
            taskId = request.taskId,
            title = request.title,
            description = request.description,
            deadline = request.deadline,
            difficulty = request.difficulty,
            done = request.done
        )

        TaskRepository.upsert(task)
    }


    fun checkTask(task: Task, filters: Iterable<Filter>) = filters.all { filter -> FilterService.checkTask(task, filter) }

    fun getAllTasks() = getFilteredTasks(emptyList())
    fun getNumOfTasks() = getAllTasks().size
}