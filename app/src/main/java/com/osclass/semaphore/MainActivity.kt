package com.osclass.semaphore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.osclass.semaphore.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val buffer = Array<Message?>(N) { null }   // 크기가 N(4)인 원형 버퍼 (배열)
    private var _in = 0
    private var _out = 0
    private val mutexP: Semaphore = MutexP()
    private val mutexC: Semaphore = MutexC()
    private val nrfull: Semaphore = Nrfull()
    private val nrempty: Semaphore = Nrempty()
    private var messageNumber: Int = 1

    private val mainDispatcher = Dispatchers.Main
    private val defaultDispatcher = Dispatchers.Default
    private val mainScope = CoroutineScope(mainDispatcher)
    private val defaultScope = CoroutineScope(defaultDispatcher)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            textIn.text = "0"
            textOut.text = "0"
            textMutexp.text = mutexP.s.toString()
            textMutexc.text = mutexC.s.toString()
            textNrfull.text = nrfull.s.toString()
            textNrempty.text = nrempty.s.toString()
        }

        binding.recyclerviewMutexp.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        binding.recyclerviewMutexp.adapter = QueueAdapter(mutexP.queue)

        binding.recyclerviewMutexc.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewMutexc.adapter = QueueAdapter(mutexC.queue)

        binding.recyclerviewNrfull.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        binding.recyclerviewNrfull.adapter = QueueAdapter(nrfull.queue)

        binding.recyclerviewNrempty.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewNrempty.adapter = QueueAdapter(nrempty.queue)


        binding.buttonProducer.setOnClickListener {
            produce()
        }
        binding.buttonConsumer.setOnClickListener {
            consume()
        }
    }


    private fun produce() {
        mainScope.launch {
            val m = Message("M${messageNumber++}") // Create a new message : m
            P(mutexP)
            P(nrempty)
            buffer[_in] = m; setBufferInView(m)
            _in = (_in + 1) % N; binding.textIn.text = _in.toString()
            V(nrfull)
            V(mutexP)
        }
    }

    private fun consume() {
        mainScope.launch {
            P(mutexC)
            P(nrfull)
            val m = buffer[_out]; setBufferOutView()       // Consume message m
            _out = (_out + 1) % N; binding.textOut.text = _out.toString()
            V(nrempty)
            V(mutexC)
        }
    }

    /** P(S) 연산 */
    private suspend fun P(S: Semaphore) {
        if (S.s > 0) {
            S.s -= 1
            setSemaphoreView(S)
        } else {
            val mutex = Mutex().apply { lock() }
            S.queue.add(mutex)
            setSemaphoreView(S)
            mutex.lock()        // 작업 대기
        }
    }

    /** V(S) 연산 */
    private fun V(S: Semaphore) {
        if (S.queue.isNotEmpty()) {
            val mutex = S.queue.remove()
            mutex.unlock()      // 작업 재개
        } else {
            S.s += 1
        }
        setSemaphoreView(S)
    }

    private fun setSemaphoreView(S: Semaphore) {
        when (S) {
            is MutexP -> {
                binding.textMutexp.text = S.s.toString()
                binding.recyclerviewMutexp.adapter = QueueAdapter(S.queue)
            }

            is MutexC -> {
                binding.textMutexc.text = S.s.toString()
                binding.recyclerviewMutexc.adapter = QueueAdapter(S.queue)
            }

            is Nrfull -> {
                binding.textNrfull.text = S.s.toString()
                binding.recyclerviewNrfull.adapter = QueueAdapter(S.queue)
            }

            is Nrempty -> {
                binding.textNrempty.text = S.s.toString()
                binding.recyclerviewNrempty.adapter = QueueAdapter(S.queue)
            }
        }
    }

    private fun setBufferInView(message: Message) {
        when (_in) {
            0 -> {
                binding.textBuffer0.text = message.content
            }

            1 -> {
                binding.textBuffer1.text = message.content
            }

            2 -> {
                binding.textBuffer2.text = message.content
            }

            3 -> {
                binding.textBuffer3.text = message.content
            }
        }
    }

    private fun setBufferOutView() {
        when (_out) {
            0 -> {
                binding.textBuffer0.text = ""
            }

            1 -> {
                binding.textBuffer1.text = ""
            }

            2 -> {
                binding.textBuffer2.text = ""
            }

            3 -> {
                binding.textBuffer3.text = ""
            }
        }
    }
}

