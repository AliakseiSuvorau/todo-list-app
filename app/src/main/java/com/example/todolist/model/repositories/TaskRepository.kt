package com.example.todolist.model.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.todolist.model.dtos.tags.Tag
import com.example.todolist.model.dtos.tasks.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object TaskRepository : Repository<Task> {
    private lateinit var db: SQLiteDatabase
    private lateinit var tasks: MutableCollection<Task>

    fun init(database: SQLiteDatabase) {
        db = database
        tasks = load()
        linkTags()
    }

    override fun getAll() = tasks

    override fun delete(id: Int) {
        db.delete(
            "tasks",
            "task_id = ?",
            arrayOf(id.toString())
        )

        tasks.removeIf { t ->
            t.taskId == id
        }
    }

    override fun upsert(task: Task): Int {
        // No tag links are updated here!

        val values = ContentValues().apply {
            put("title", task.title)
            put("description", task.description)
            put("deadline", task.deadline.toString())
            put("difficulty", task.difficulty)
            put("done", if (task.done) 1 else 0)
        }

        val rowsUpdated = db.update(
            "tasks",
            values,
            "task_id = ?",
            arrayOf(task.taskId.toString())
        )

        if (rowsUpdated == 0) {
            val newTaskId = (db.insert("tasks", null, values)).toInt()
            task.taskId = newTaskId
            tasks.add(task)
            return newTaskId
        }

        tasks.forEach { t ->
            if (t.taskId == task.taskId) {
                t.title = task.title
                t.description = task.description
                t.deadline = task.deadline
                t.difficulty = task.difficulty
                t.done = task.done
            }
        }

        return task.taskId
    }

    private fun load(): MutableCollection<Task> {
        val tasks = mutableListOf<Task>()
        val cursor = db.query(
            "tasks",
            null,
            null,
            null,
            null,
            null,
            null,
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val deadlineString = c.getStringOrNull(c.getColumnIndexOrThrow("deadline"))
                var deadline: Instant? = null
                if (deadlineString != "null") {
                    deadline = Instant.parse(deadlineString)
                }

                val task = Task(
                    taskId = c.getInt(c.getColumnIndexOrThrow("task_id")),
                    title = c.getString(c.getColumnIndexOrThrow("title")),
                    description = c.getStringOrNull(c.getColumnIndexOrThrow("description")),
                    deadline = deadline,
                    difficulty = c.getIntOrNull(c.getColumnIndexOrThrow("difficulty")),
                    done = c.getInt(c.getColumnIndexOrThrow("done")) == 1,
                )
                tasks.add(task)
            }
        }

        return tasks
    }

    private fun linkTags() {
        val query = """
                SELECT tags.* FROM tags
                INNER JOIN task_tag ON tags.tag_id = task_tag.tag_id
                WHERE task_id = ?
            """.trimIndent()

        for (task in tasks) {
            val cursor = db.rawQuery(query, arrayOf(task.taskId.toString()))
            val tags = mutableListOf<Tag>()

            cursor.use { c ->
                while (c.moveToNext()) {
                    val deadlineString = c.getStringOrNull(c.getColumnIndexOrThrow("deadline"))
                    var deadline: Instant? = null
                    if (deadlineString != "null") {
                        deadline = Instant.parse(deadlineString)
                    }

                    val tag = Tag(
                        tagId = c.getInt(c.getColumnIndexOrThrow("tag_id")),
                        name = c.getString(c.getColumnIndexOrThrow("name")),
                        deadline = deadline,
                        difficulty = c.getIntOrNull(c.getColumnIndexOrThrow("difficulty")),
                        done = c.getIntOrNull(c.getColumnIndexOrThrow("done"))?.let { it == 1 }
                    )
                    tags.add(tag)
                }
            }

            task.tags = tags
        }
    }

    fun linkTag(tagId: Int, taskId: Int) {
        val values = ContentValues().apply {
            put("task_id", taskId.toString())
            put("tag_id", tagId.toString())
        }

        db.insert(
            "task_tag",
            null,
            values
        )

        val tag = TagRepository.getById(tagId) ?: throw IllegalStateException("Linking null tag to a task")

        tasks.filter { t ->
            t.taskId == taskId
        }.forEach { t ->
            t.tags.add(tag)
        }
    }

    fun unlinkTag(tagId: Int) {
        tasks.forEach { task ->
            task.tags.removeIf { tag ->
                tag.tagId == tagId
            }
        }
    }
}
