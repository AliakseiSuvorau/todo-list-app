package com.example.todolist.model.repositories

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.example.todolist.model.dtos.filters.Filter
import com.example.todolist.model.dtos.tags.Tag
import java.time.Instant

object FilterRepository : Repository<Filter> {
    private lateinit var db: SQLiteDatabase
    private lateinit var filters: MutableCollection<Filter>

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

        filters.removeIf { f ->
            f.filterId == id
        }
    }

    override fun upsert(filter: Filter): Int {
        val values = ContentValues().apply {
            put("name", filter.name)
        }

        val rowsUpdated = db.update(
            "filters",
            values,
            "filter_id = ?",
            arrayOf(filter.filterId.toString())
        )

        if (rowsUpdated == 0) {
            val newFilterId = (db.insert("filters", null, values)).toInt()
            filter.filterId = newFilterId
            filters.add(filter)
            return newFilterId
        }

        filters.removeIf { f ->
            f.filterId == filter.filterId
        }
        filters.add(filter)

        return filter.filterId
    }

    private fun load(): MutableCollection<Filter> {
        val filters = mutableListOf<Filter>()
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
                val filter = Filter(
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
                SELECT * FROM tags
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
                        deadline = c.getStringOrNull(c.getColumnIndexOrThrow("deadline"))?.let { Instant.parse(it) },
                        urgency = c.getIntOrNull(c.getColumnIndexOrThrow("urgency")),
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

        val tag = TagRepository.getById(tagId) ?: throw IllegalStateException("Linking null tag to a filter")

        filters.filter { f ->
            f.filterId == filterId
        }.forEach { f ->
            f.tags.add(tag)
        }
    }

    fun unlinkTag(tagId: Int) {
        filters.forEach { filter ->
            filter.tags.removeIf { tag ->
                tag.tagId == tagId
            }
        }
    }
}
