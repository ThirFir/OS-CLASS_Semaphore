package com.osclass.semaphore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osclass.semaphore.databinding.ItemQueueBinding
import kotlinx.coroutines.sync.Mutex
import java.util.Queue

class QueueAdapter(private val queue: Queue<Mutex>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        QueueViewHolder(ItemQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = queue.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as QueueViewHolder).binding.textBuffer.text = "Q${position + 1}"

    }


    inner class QueueViewHolder(val binding: ItemQueueBinding): RecyclerView.ViewHolder(binding.root)
}