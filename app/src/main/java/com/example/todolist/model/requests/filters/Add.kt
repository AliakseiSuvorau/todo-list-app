package com.example.todolist.model.requests.filters

import java.time.Instant

data class AddFilterRequest(
    val name: String,
    val userTagIds: Collection<Int>,
    val deadline: Instant?,
    val difficulty: Int?,
    val done: Boolean?,
)
