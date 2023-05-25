package com.osclass.semaphore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.osclass.semaphore.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val buffer = Array<Message?>(N) { null }   // 크기가 N(4)인 원형 버퍼 (배열)
    private var _in = 0
    private var _out = 0
    private val mutexP = Semaphore(1)
    private val mutexC = Semaphore(1)
    private val nrfull = Semaphore(0)
    private val nrempty = Semaphore(N)      // N = 4
    private var messageNumber: Int = 1

    private val mainDispatcher = Dispatchers.Main
    private val mainScope = CoroutineScope(mainDispatcher)

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
        binding.recyclerviewMutexp.adapter = QueueAdapter(mutexP)

        binding.recyclerviewMutexc.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewMutexc.adapter = QueueAdapter(mutexC)

        binding.recyclerviewNrfull.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        binding.recyclerviewNrfull.adapter = QueueAdapter(nrfull)

        binding.recyclerviewNrempty.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewNrempty.adapter = QueueAdapter(nrempty)


        // 생산자 버튼 클릭 이벤트
        binding.buttonProducer.setOnClickListener {
            produce()
        }

        // 소비자 버튼 클릭 이벤트
        binding.buttonConsumer.setOnClickListener {
            consume()
        }
    }


    private fun produce() {
        mainScope.launch {
            val m = Message("M${messageNumber++}") // Create a new message : m
            P(mutexP)
            P(nrempty)
            buffer[_in] = m;
            setBufferInView(m)      // UI 갱신
            _in = (_in + 1) % N;
            binding.textIn.text = _in.toString()        // UI 갱신
            V(nrfull)
            V(mutexP)
        }
    }

    private fun consume() {
        mainScope.launch {
            P(mutexC)
            P(nrfull)
            val m = buffer[_out];       // Consume message m
            setBufferOutView()          // UI 갱신
            _out = (_out + 1) % N;
            binding.textOut.text = _out.toString()      // UI 갱신
            V(nrempty)
            V(mutexC)
        }
    }

    /** P(S) 연산 */
    private suspend fun P(S: Semaphore) {
        if (S.s > 0) {
            S.s -= 1
            setSemaphoreView()     // UI 갱신
        } else {
            val mutex = Mutex().apply { lock() }
            S.add(mutex) {
                setSemaphoreView()  // UI 갱신
                mutex.lock()        // 작업 중지
            }
        }
    }

    /** V(S) 연산 */
    private fun V(S: Semaphore) {
        if (S.queue.isNotEmpty()) {
            S.remove() {
                setSemaphoreView()  // UI 갱신
                it.unlock()         // 작업 재개
            }
        } else {
            S.s += 1
            setSemaphoreView()      // UI 갱신
        }
    }


    /** Semaphore UI 갱신 */
    private fun setSemaphoreView() {
        binding.textMutexp.text = mutexP.s.toString()
        binding.textMutexc.text = mutexC.s.toString()
        binding.textNrfull.text = nrfull.s.toString()
        binding.textNrempty.text = nrempty.s.toString()

        binding.recyclerviewMutexp.adapter = QueueAdapter(mutexP)
        binding.recyclerviewMutexc.adapter = QueueAdapter(mutexC)
        binding.recyclerviewNrfull.adapter = QueueAdapter(nrfull)
        binding.recyclerviewNrempty.adapter = QueueAdapter(nrempty)
    }

    /** 원형 버퍼 UI 갱신 */
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

    /** 원형 버퍼 UI 갱신 */
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

