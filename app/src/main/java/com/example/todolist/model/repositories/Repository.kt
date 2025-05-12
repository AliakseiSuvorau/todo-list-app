package com.example.todolist.model.repositories

interface Repository <T> {
    fun getAll(): Collection<T>
    fun upsert(obj: T): Int
    fun delete(id: Int)
}