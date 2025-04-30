package com.example.todolist.model.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.todolist.model.dtos.tags.Tag
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object TagRepository : Repository<Tag> {
    private lateinit var db: SQLiteDatabase
    private lateinit var tags: MutableCollection<Tag>

    fun init(database: SQLiteDatabase) {
        db = database
        tags = load()
    }

    private fun load(): MutableCollection<Tag> {
        val tags = mutableListOf<Tag>()
        val cursor = db.query(
            "tags",
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

                val tag = Tag(
                    tagId = c.getInt(c.getColumnIndexOrThrow("tag_id")),
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    deadline = deadline,
                    difficulty = c.getIntOrNull(c.getColumnIndexOrThrow("difficulty")),
                    done = c.getIntOrNull(c.getColumnIndexOrThrow("done")) == 1,
                )
                tags.add(tag)
            }
        }

        return tags
    }

    override fun getAll() = tags

    override fun delete(id: Int) {
        db.delete(
            "tags",
            "tag_id = ?",
            arrayOf(id.toString())
        )

        db.delete(
            "task_tag",
            "tag_id = ?",
            arrayOf(id.toString())
        )

        db.delete(
            "tag_filter",
            "tag_id = ?",
            arrayOf(id.toString())
        )

        tags.removeIf { t ->
            t.tagId == id
        }

        TaskRepository.unlinkTag(id)
        FilterRepository.unlinkTag(id)
    }

    override fun upsert(tag: Tag): Int {
        val values = ContentValues().apply {
            put("name", tag.name)
            put("deadline", tag.deadline.toString())
            put("difficulty", tag.difficulty)
            put("done", if (tag.done == true) 1 else 0)
        }

        val rowsUpdated = db.update(
            "tags",
            values,
            "tag_id = ?",
            arrayOf(tag.tagId.toString())
        )

        if (rowsUpdated == 0) {
            val newTagId = (db.insert("tags", null, values)).toInt()
            tag.tagId = newTagId
            tags.add(tag)
            return newTagId
        }

        tags.removeIf { t ->
            t.tagId == tag.tagId
        }
        tags.add(tag)

        return tag.tagId
    }

    fun getById(id: Int): Tag? {
        val cursor = db.query(
            "tags",
            null,
            "tag_id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        )

        cursor.use { c ->
            if (c.moveToNext()) {
                val deadlineString = c.getStringOrNull(c.getColumnIndexOrThrow("deadline"))
                var deadline: Instant? = null
                if (deadlineString != "null") {
                    deadline = Instant.parse(deadlineString)
                }

                return Tag(
                    tagId = id,
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    deadline = deadline,
                    difficulty = c.getIntOrNull(c.getColumnIndexOrThrow("difficulty")),
                    done = c.getIntOrNull(c.getColumnIndexOrThrow("done")) == 1
                )
            }
            return null
        }
    }

    fun getMultipleByIds(ids: Iterable<Int>): MutableCollection<Tag> {
        val placeholders = ids.joinToString(",") { "?" }

        val cursor = db.rawQuery(
            "SELECT * FROM tags WHERE tags.tag_id IN ($placeholders)",
            ids.map { id -> id.toString() }.toTypedArray()
        )

        val tags = mutableListOf<Tag>()
        cursor.use { c ->
            while (c.moveToNext()) {
                val deadlineString = c.getStringOrNull(c.getColumnIndexOrThrow("deadline"))
                var deadline: Instant? = null
                if (deadlineString != "null") {
                    deadline = Instant.parse(deadlineString)
                }

                tags.add(Tag(
                    tagId = c.getInt(c.getColumnIndexOrThrow("tag_id")),
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                    deadline = deadline,
                    difficulty = c.getIntOrNull(c.getColumnIndexOrThrow("difficulty")),
                    done = c.getIntOrNull(c.getColumnIndexOrThrow("done")) == 1,
                ))
            }
        }

        return tags
    }
}