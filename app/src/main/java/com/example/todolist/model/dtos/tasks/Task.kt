package com.example.todolist.model.dtos.tasks

import com.example.todolist.model.dtos.tags.Tag
import java.time.Instant

data class Task(
    var taskId: Int = -1,
    var title: String,
    var description: String?,
    var deadline: Instant?,
    var difficulty: Int?,
    var done: Boolean,
    var tags: MutableCollection<Tag> = mutableListOf(),
)


