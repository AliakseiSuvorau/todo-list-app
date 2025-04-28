package com.example.todolist.model.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.todolist.model.dtos.filters.TaskFilter
import com.example.todolist.model.dtos.tags.Tag
import kotlin.time.Duration

object FilterRepository : Repository<TaskFilter> {
    private lateinit var db: SQLiteDatabase
    private lateinit var filters: Collection<TaskFilter>

    fun init(database: SQLiteDatabase) {
        db = database
        filters = load()
        linkTags()
    }

    override fun getAll() = filters

    override fun delete(id: Int) {
        db.delete(
            "filters",
            "filter_id = ?",
            arrayOf(id.toString())
        )
    }

    override fun upsert(filter: TaskFilter) {
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM filters WHERE filter_id = ?",
            arrayOf(filter.filterId.toString())
        )

        // If exists then return
        cursor.use { c ->
            if (c.moveToNext()) {
                val count = c.getInt(0)
                if (count > 0) {
                    return
                }
            }
        }

        db.insert("filters", null, ContentValues())
    }

    private fun load(): Collection<TaskFilter> {
        val filters = mutableListOf<TaskFilter>()
        val cursor = db.query(
            "filters",
            null,
            null,
            null,
            null,
            null,
            null,
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val filter = TaskFilter(
                    filterId = c.getInt(c.getColumnIndexOrThrow("filter_id")),
                    name = c.getString(c.getColumnIndexOrThrow("name")),
                )
                filters.add(filter)
            }
        }

        return filters
    }

    private fun linkTags() {
        val query = """
                SELECT tags.* FROM tags
                INNER JOIN tag_filter ON tags.tag_id = tag_filter.tag_id
                WHERE filter_id = ?
            """.trimIndent()

        for (filter in filters) {
            val cursor = db.rawQuery(query, arrayOf(filter.filterId.toString()))
            val tags = mutableListOf<Tag>()

            cursor.use { c ->
                while (c.moveToNext()) {
                    val tag = Tag(
                        tagId = c.getInt(c.getColumnIndexOrThrow("tag_id")),
                        name = c.getString(c.getColumnIndexOrThrow("name")),
                        deadline = c.getStringOrNull(c.getColumnIndexOrThrow("deadline"))?.let { Duration.parse(it) },
                        difficulty = c.getIntOrNull(c.getColumnIndexOrThrow("difficulty")),
                        done = c.getIntOrNull(c.getColumnIndexOrThrow("done"))?.let { it == 1 }
                    )
                    tags.add(tag)
                }
            }

            filter.tags = tags
        }
    }

    fun linkTag(tagId: Int, filterId: Int) {
        val values = ContentValues().apply {
            put("filter_id", filterId.toString())
            put("tag_id", tagId.toString())
        }

        db.insert(
            "tag_filter",
            null,
            values
        )
    }
}
