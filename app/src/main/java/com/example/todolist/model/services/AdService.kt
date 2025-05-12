package com.example.todolist.model.services

import com.example.todolist.adapters.TaskItem
import com.example.todolist.adapters.TasksAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AdService(
    private val tasksAdapter: TasksAdapter,
    private val adProvider: suspend () -> TaskItem.AdEntry
) {
    suspend fun startAdCycle() {
        while (true) {
            // Get ad from background thread
            val ad = withContext(Dispatchers.IO) {
                adProvider()
            }

            val adId = tasksAdapter.addAd(ad)

            delay(10_000)

            tasksAdapter.removeAd(adId)

            delay(50_000)
        }
    }
}