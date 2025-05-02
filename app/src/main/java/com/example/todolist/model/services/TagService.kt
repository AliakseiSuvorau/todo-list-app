package com.example.todolist.model.services

import com.example.todolist.model.dtos.tags.Tag
import com.example.todolist.model.dtos.tasks.Task
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.requests.tags.AddTagRequest
import java.time.Instant

object TagService {
    private const val DEADLINE_TAG_NAME = ".deadline"
    private const val URGENCY_TAG_NAME = ".urgency"
    private const val COMPLETION_TAG_NAME = ".done"

    private val toggledUserTags = mutableListOf<Tag>()

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

    fun addUrgencyTag(d: Int): Tag {
        val newTag = Tag(
            name = URGENCY_TAG_NAME,
            urgency = d,
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
    fun getDeadlineTag(deadline: Instant) = Tag(name = DEADLINE_TAG_NAME, deadline = deadline)

    fun checkTag(task: Task, filterTag: Tag, showTasksWithoutDeadline: Boolean = true): Boolean {
        return when (filterTag.name) {
            DEADLINE_TAG_NAME -> checkDeadline(task.deadline, filterTag.deadline!!, showTasksWithoutDeadline)
            URGENCY_TAG_NAME -> checkUrgency(
                task.urgency!!,
                filterTag.urgency!!
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

    private fun checkDeadline(taskDeadline: Instant?, deadline: Instant, showTasksWithoutDeadline: Boolean) =
        (showTasksWithoutDeadline && taskDeadline == null) || (taskDeadline != null &&  taskDeadline < deadline)

    private fun checkUrgency(taskUrgency: Int, urgency: Int) = taskUrgency == urgency
    private fun checkCompletion(taskIsDone: Boolean, done: Boolean) = taskIsDone == done
    private fun checkUserTag(taskTagName: String, tagName: String) = taskTagName == tagName

    fun getUserTags() = TagRepository.getAll()
        .filterNot { it.name == DEADLINE_TAG_NAME || it.name == URGENCY_TAG_NAME || it.name == COMPLETION_TAG_NAME }

    fun removeTagFromToggled(tag: Tag) {
        toggledUserTags.remove(tag)
    }

    fun addTagToToggled(tag: Tag) {
        if (!toggledUserTags.contains(tag)) {
            toggledUserTags.add(tag)
        }
    }

    fun getToggledUserTags() = toggledUserTags
    fun clearToggledUserTags() = toggledUserTags.clear()
}
