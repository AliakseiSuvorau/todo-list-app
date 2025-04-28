package com.example.todolist.model.services

import com.example.todolist.model.dtos.tags.Tag
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.requests.tags.AddTagRequest
import kotlin.time.Duration

object TagService {
    private const val DEADLINE_TAG_NAME = ".deadline"
    private const val DIFFICULTY_TAG_NAME = ".difficulty"
    private const val COMPLETION_TAG_NAME = ".done"

    fun addTag(request: AddTagRequest) = TagRepository.upsert(
        Tag(
            name = request.name
        )
    )

    fun addDeadlineTag(d: Duration): Tag {
        val newTag = Tag(
            name = DEADLINE_TAG_NAME,
            deadline = d,
        )
        val newTagId = TagRepository.upsert(tag = newTag)
        newTag.tagId = newTagId
        return newTag
    }

    fun addDifficultyTag(d: Int): Tag {
        val newTag = Tag(
            name = DIFFICULTY_TAG_NAME,
            difficulty = d,
        )
        val newTagId = TagRepository.upsert(tag = newTag)
        newTag.tagId = newTagId
        return newTag
    }

    fun addCompletionTag(c: Boolean): Tag {
        val newTag = Tag(
            name = COMPLETION_TAG_NAME,
            done = c,
        )
        val newTagId = TagRepository.upsert(tag = newTag)
        newTag.tagId = newTagId
        return newTag
    }
}
