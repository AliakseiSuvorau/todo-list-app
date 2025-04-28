package com.example.todolist.model.repositories

interface Repository <T> {
    fun getAll(): Collection<T>
    fun upsert(obj: T)
    fun delete(id: Int)
}