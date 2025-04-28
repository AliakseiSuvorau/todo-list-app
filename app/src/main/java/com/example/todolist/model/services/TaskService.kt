package com.example.todolist.model.services

import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.repositories.TaskRepository
import com.example.todolist.model.requests.tasks.AddTaskRequest

object TaskService {
    fun addTask(request: AddTaskRequest): Int {
        val tags = TagRepository.getMultipleByIds(request.userTagIds)

        val newTaskId = TaskRepository.upsert(
            Task(
                title = request.title,
                description = request.description,
                deadline = request.deadline,
                difficulty = request.difficulty,
                done = request.done,
                tags = tags,
            )
        )
        for (tagId in request.userTagIds) {
            TaskRepository.linkTag(tagId, newTaskId)
        }
        return newTaskId
    }
}