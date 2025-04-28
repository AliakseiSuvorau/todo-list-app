package com.example.todolist.model.dtos.filters

import com.example.todolist.model.dtos.tags.Tag

data class Filter(
    var filterId: Int = -1,
    val name: String,
    var tags: MutableCollection<Tag> = mutableListOf(),
)
