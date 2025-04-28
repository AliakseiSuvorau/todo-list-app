package com.example.todolist.model.dtos.tags

import kotlin.time.Duration

data class Tag(
    var tagId: Int = -1,
    val name: String,
    val deadline: Duration? = null,
    val difficulty: Int? = null,
    val done: Boolean? = null,
)
