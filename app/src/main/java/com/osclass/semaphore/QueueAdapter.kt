package com.osclass.semaphore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.osclass.semaphore.databinding.ItemQueueBinding

class QueueAdapter(private val semaphore: Semaphore): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        QueueViewHolder(ItemQueueBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = semaphore.queue.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as QueueViewHolder).binding.textBuffer.text = (semaphore.queueFirstNumber + position).toString()

    }


    inner class QueueViewHolder(val binding: ItemQueueBinding): RecyclerView.ViewHolder(binding.root)
}