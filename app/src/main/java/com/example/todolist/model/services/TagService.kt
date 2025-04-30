package com.example.todolist.model.services

import com.example.todolist.model.dtos.tags.Tag
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.requests.tags.AddTagRequest
import java.time.Instant

object TagService {
    private const val DEADLINE_TAG_NAME = ".deadline"
    private const val DIFFICULTY_TAG_NAME = ".difficulty"
    private const val COMPLETION_TAG_NAME = ".done"

    fun addTag(request: AddTagRequest): Tag {
        val newTag = Tag(name = request.name)
        val newTagId = TagRepository.upsert(newTag)
        newTag.tagId = newTagId
        return newTag
    }

    fun addDeadlineTag(d: Instant): Tag {
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

    fun getCompletionTag(done: Boolean) = Tag(name = COMPLETION_TAG_NAME, done = done)

    fun checkTag(task: Task, filterTag: Tag): Boolean {
        return when (filterTag.name) {
            DEADLINE_TAG_NAME -> checkDeadline(task.deadline, filterTag.deadline!!)
            DIFFICULTY_TAG_NAME -> checkDifficulty(
                task.difficulty!!,
                filterTag.difficulty!!
            )
            COMPLETION_TAG_NAME -> checkCompletion(task.done, filterTag.done!!)
            else -> {
                for (taskTag in task.tags) {
                    if (checkUserTag(taskTag.name, filterTag.name)) {
                        return true
                    }
                }
                return false
            }
        }
    }

    private fun checkDeadline(taskDeadline: Instant?, deadline: Instant) =
        taskDeadline == null || taskDeadline < deadline

    private fun checkDifficulty(taskDifficulty: Int, difficulty: Int) = taskDifficulty == difficulty
    private fun checkCompletion(taskIsDone: Boolean, done: Boolean) = taskIsDone == done
    private fun checkUserTag(taskTagName: String, tagName: String) = taskTagName == tagName

    fun getUserTags() = TagRepository.getAll()
        .filterNot { it.name == DEADLINE_TAG_NAME || it.name == DIFFICULTY_TAG_NAME || it.name == COMPLETION_TAG_NAME }
}
