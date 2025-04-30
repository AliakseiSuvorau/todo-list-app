package com.example.todolist.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseInitializer(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "task_manager"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) {
            throw IllegalStateException("No database passed to create method.")
        }

        db.execSQL(
            """
            CREATE TABLE tags (
                tag_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                deadline TIMESTAMP,
                difficulty INTEGER,
                done BOOLEAN
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE tasks (
                task_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT,
                deadline TIMESTAMP,
                difficulty INTEGER,
                done BOOLEAN NOT NULL DEFAULT 0
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE task_tag (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                task_id INTEGER NOT NULL,
                tag_id INTEGER NOT NULL,
                FOREIGN KEY (task_id) REFERENCES tasks(task_id),
                FOREIGN KEY (tag_id) REFERENCES tags(tag_id)
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE filters (
                filter_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            );
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE tag_filter (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tag_id INTEGER NOT NULL,
                filter_id INTEGER NOT NULL,
                FOREIGN KEY (tag_id) REFERENCES tags(tag_id),
                FOREIGN KEY (filter_id) REFERENCES filters(filter_id)
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (db == null) {
            throw IllegalStateException("No database passed to update method!")
        }

        db.execSQL("DROP TABLE IF EXISTS tag_filter")
        db.execSQL("DROP TABLE IF EXISTS filters")
        db.execSQL("DROP TABLE IF EXISTS task_tag")
        db.execSQL("DROP TABLE IF EXISTS tasks")
        db.execSQL("DROP TABLE IF EXISTS tags")
        onCreate(db)
    }
}