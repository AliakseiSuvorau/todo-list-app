package com.example.todolist.model.services

import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.repositories.FilterRepository
import com.example.todolist.model.repositories.TagRepository
import com.example.todolist.model.requests.filters.AddFilterRequest

object FilterService {
    fun addFilter(request: AddFilterRequest) {
        val tags = TagRepository.getMultipleByIds(request.userTagIds)
        if (request.deadline != null) {
            tags.add(TagService.addDeadlineTag(request.deadline))
        }
        if (request.difficulty != null) {
            tags.add(TagService.addDifficultyTag(request.difficulty))
        }
        if (request.done != null) {
            tags.add(TagService.addCompletionTag(request.done))
        }

        val filter = Filter(
            name = request.name,
            tags = tags,
        )

        val newFilterId = FilterRepository.upsert(filter)
        for (userTagId in request.userTagIds) {
            FilterRepository.linkTag(userTagId, newFilterId)
        }
    }

//    fun filterTasks(tasks: Iterable<Task>) = tasks.filter { t -> check(t) }
//    private fun check(task: Task) = task.tags.all { tag -> checkTag(task, tag) }
//    private fun checkTag(task: Task, filterTag: Tag): Boolean {
//        for (taskTag in task.tags) {
//            if (when(filterTag.name) {
//                    DEADLINE_TAG_NAME -> checkDeadline(task.deadline!!, filterTag.deadline!!)
//                    DIFFICULTY_TAG_NAME -> checkDifficulty(task.difficulty!!, filterTag.difficulty!!)
//                    COMPLETION_TAG_NAME -> checkCompletion(task.done, filterTag.done!!)
//                    else -> checkUserTag(taskTag.name, filterTag.name)
//                }) {
//                return true
//            }
//        }
//        return false
//    }
//    private fun checkDeadline(taskDeadline: Duration, deadline: Duration)= taskDeadline.inWholeMilliseconds < deadline.inWholeMilliseconds
//    private fun checkDifficulty(taskDifficulty: Int, difficulty: Int) = taskDifficulty == difficulty
//    private fun checkCompletion(taskIsDone: Boolean, done: Boolean) = taskIsDone == done
//    private fun checkUserTag(taskTagName: String, tagName: String) = taskTagName == tagName
}