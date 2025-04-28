package com.example.todolist.model.requests.tasks

import kotlin.time.Duration

data class AddTaskRequest(
    val title: String,
    val description: String?,
    val deadline: Duration?,
    val difficulty: Int?,
    val done: Boolean = false,
    val userTagIds: Iterable<Int>,
)
