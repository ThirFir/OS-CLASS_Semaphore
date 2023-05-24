package com.osclass.semaphore

import kotlinx.coroutines.selects.SelectClause2
import kotlinx.coroutines.sync.Mutex
import java.util.LinkedList
import java.util.Queue

const val N = 4

class MutexP : Semaphore {
    override var s: Int = 1
    override val queue: Queue<Mutex> = LinkedList()
}

class MutexC : Semaphore {
    override var s: Int = 1
    override val queue: Queue<Mutex> = LinkedList()
}

class Nrfull : Semaphore {
    override var s: Int = 0
    override val queue: Queue<Mutex> = LinkedList()
}

class Nrempty : Semaphore {
    override var s: Int = N
    override val queue: Queue<Mutex> = LinkedList()
}

interface Semaphore {
    var s: Int
    val queue: Queue<Mutex>
}
