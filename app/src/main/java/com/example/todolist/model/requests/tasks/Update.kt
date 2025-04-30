package com.example.todolist.model.requests.tasks

import java.time.Instant

data class UpdateTaskRequest(
    val taskId: Int,
    val title: String,
    val description: String?,
    val deadline: Instant?,
    val difficulty: Int?,
    val done: Boolean,
)
