package com.example.todolist.model.dtos.filters

import com.example.todolist.model.dtos.tags.Tag
import com.example.todolist.model.dtos.tasks.Task
import kotlin.time.Duration

interface Filter {
    fun addDeadlineTag(d: Duration)
    fun addDifficultyTag(d: Int)
    fun addCompletionTag(c: Boolean)
    fun addUserTag(t: Tag)
    fun filterTasks(tasks: Iterable<Task>): Iterable<Task>
}

data class TaskFilter(
    val filterId: Int = -1,
    val name: String,
    var tags: MutableCollection<Tag> = mutableListOf(),
) : Filter {
    private val deadlineTagName = ".deadline"
    private val difficultyTagName = ".difficulty"
    private val completionTagName = ".done"

    private fun addTag(t: Tag) {
        tags.add(t)
        // TODO: Add link through repository to tag_filter table
    }
    override fun addDeadlineTag(d: Duration) = addTag(Tag(name = deadlineTagName, deadline = d))
    override fun addDifficultyTag(d: Int) = addTag(Tag(name = difficultyTagName, difficulty = d))
    override fun addCompletionTag(c: Boolean) = addTag(Tag(name = completionTagName, done = c))
    override fun addUserTag(t: Tag) = addTag(t)

    override fun filterTasks(tasks: Iterable<Task>) = tasks.filter { t -> check(t) }
    private fun check(task: Task) = tags.all { tag -> checkTag(task, tag) }
    private fun checkTag(task: Task, filterTag: Tag): Boolean {
        for (taskTag in task.tags) {
            if (when(filterTag.name) {
                deadlineTagName -> checkDeadline(task.deadline!!, filterTag.deadline!!)
                difficultyTagName -> checkDifficulty(task.difficulty!!, filterTag.difficulty!!)
                completionTagName -> checkCompletion(task.done, filterTag.done!!)
                else -> checkUserTag(taskTag.name, filterTag.name)
            }) {
                return true
            }
        }
        return false
    }
    private fun checkDeadline(taskDeadline: Duration, deadline: Duration)= taskDeadline.inWholeMilliseconds < deadline.inWholeMilliseconds
    private fun checkDifficulty(taskDifficulty: Int, difficulty: Int) = taskDifficulty == difficulty
    private fun checkCompletion(taskIsDone: Boolean, done: Boolean) = taskIsDone == done
    private fun checkUserTag(taskTagName: String, tagName: String) = taskTagName == tagName
}
