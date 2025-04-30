package com.example.todolist.model.dtos.tags

import java.time.Instant

data class Tag(
    var tagId: Int = -1,
    val name: String,
    val deadline: Instant? = null,
    val difficulty: Int? = null,
    val done: Boolean? = null,
)
