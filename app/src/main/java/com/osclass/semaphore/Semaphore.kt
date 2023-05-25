package com.osclass.semaphore

import kotlinx.coroutines.sync.Mutex
import java.util.LinkedList
import java.util.Queue

const val N = 4

class Semaphore(var s: Int) {
    val queue: Queue<Mutex> = LinkedList()
    var queueFirstNumber: Int = 1

    suspend fun add(mutex: Mutex, onAddedCallback: suspend() -> Unit) {
        queue.add(mutex)
        onAddedCallback()
    }
    fun remove(onRemovedCallback: (Mutex) -> Unit) {
        val mutex = queue.remove()
        queueFirstNumber++
        onRemovedCallback(mutex)
    }
}
