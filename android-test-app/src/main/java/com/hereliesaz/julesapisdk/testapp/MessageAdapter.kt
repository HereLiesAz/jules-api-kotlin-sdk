package com.hereliesaz.julesapisdk.testapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = when (MessageType.values()[viewType]) {
            MessageType.USER -> layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false)
            MessageType.BOT -> layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            MessageType.ERROR -> layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
        }
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(message: Message) {
            val textView: TextView
            when (message.type) {
                MessageType.USER -> {
                    textView = itemView.findViewById(android.R.id.text2)
                    textView.text = message.text
                }
                MessageType.BOT -> {
                    textView = itemView.findViewById(android.R.id.text1)
                    textView.text = message.text
                }
                MessageType.ERROR -> {
                    textView = itemView.findViewById(android.R.id.text1)
                    textView.text = message.text
                    textView.setTextColor(Color.RED)
                }
            }
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
