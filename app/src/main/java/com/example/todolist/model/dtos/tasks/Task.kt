package com.example.todolist.model.dtos.tasks

import com.example.todolist.model.dtos.tags.Tag
import kotlin.time.Duration

data class Task(
    var taskId: Int = -1,
    val title: String,
    val description: String?,
    val deadline: Duration?,
    val difficulty: Int?,
    val done: Boolean,
    var tags: MutableCollection<Tag> = mutableListOf(),
)


