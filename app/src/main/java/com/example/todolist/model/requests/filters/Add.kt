package com.example.todolist.model.requests.filters

import kotlin.time.Duration

data class AddFilterRequest(
    val name: String,
    val userTagIds: Collection<Int>,
    val deadline: Duration?,
    val difficulty: Int?,
    val done: Boolean?,
)
