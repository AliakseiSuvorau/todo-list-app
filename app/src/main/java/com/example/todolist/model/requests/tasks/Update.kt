package com.example.todolist.model.requests.tasks

import com.example.todolist.model.dtos.tags.Tag
import java.time.Instant

data class UpdateTaskRequest(
    val taskId: Int = -1,
    val title: String,
    val description: String?,
    val deadline: Instant?,
    val urgency: Int?,
    val done: Boolean,
    val userTags: MutableCollection<Tag> = mutableListOf(),
)
