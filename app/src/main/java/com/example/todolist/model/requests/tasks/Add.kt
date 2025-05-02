package com.example.todolist.model.requests.tasks

import java.time.Instant

data class AddTaskRequest(
    val title: String,
    val description: String?,
    val deadline: Instant?,
    val urgency: Int?,
    val done: Boolean = false,
    val userTagIds: Iterable<Int>,
)
