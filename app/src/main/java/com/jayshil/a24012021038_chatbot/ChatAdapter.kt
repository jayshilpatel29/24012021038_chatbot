package com.jayshil.a24012021038_chatbot

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private var typingPosition = -1
    private var dotCount = 1
    private var showTimestamps = false

    private val typingRunnable = object : Runnable {
        override fun run() {
            if (
                typingPosition >= 0 &&
                typingPosition < messages.size &&
                messages[typingPosition].isTyping
            ) {
                dotCount++
                if (dotCount > 3) {
                    dotCount = 1
                }
                notifyItemChanged(typingPosition)
                handler.postDelayed(this, 350)
            }
        }
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageCard: MaterialCardView = itemView.findViewById(R.id.messageCard)
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }

    fun setShowTimestamps(show: Boolean) {
        if (showTimestamps != show) {
            showTimestamps = show
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat_message,
            parent,
            false
        )
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val params = holder.messageCard.layoutParams as ConstraintLayout.LayoutParams

        if (showTimestamps && message.timestamp.isNotEmpty() && !message.isTyping) {
            holder.tvTimestamp.text = message.timestamp
            holder.tvTimestamp.visibility = View.VISIBLE
        } else {
            holder.tvTimestamp.visibility = View.GONE
        }

        if (message.isTyping) {
            val dots = "● ".repeat(dotCount).trim()
            holder.tvMessage.text = dots
            holder.messageCard.setCardBackgroundColor(Color.parseColor("#F4F4F4"))
            holder.tvMessage.setTextColor(Color.parseColor("#F0444D"))

            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET
            params.endToStart = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET
            holder.messageCard.layoutParams = params
            return
        }

        holder.tvMessage.text = message.message

        if (message.isUser) {
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET
            params.endToStart = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            holder.messageCard.layoutParams = params

            holder.messageCard.setCardBackgroundColor(Color.parseColor("#F0444D"))
            holder.tvMessage.setTextColor(Color.WHITE)
        } else {
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToEnd = ConstraintLayout.LayoutParams.UNSET
            params.endToStart = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.UNSET
            holder.messageCard.layoutParams = params

            holder.messageCard.setCardBackgroundColor(Color.parseColor("#F4F4F4"))
            holder.tvMessage.setTextColor(Color.parseColor("#222222"))
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        if (message.isTyping) {
            typingPosition = messages.size - 1
            dotCount = 1
            handler.removeCallbacks(typingRunnable)
            handler.postDelayed(typingRunnable, 350)
        }
        notifyItemInserted(messages.size - 1)
    }

    fun removeTypingMessage() {
        if (
            typingPosition >= 0 &&
            typingPosition < messages.size &&
            messages[typingPosition].isTyping
        ) {
            messages.removeAt(typingPosition)
            notifyItemRemoved(typingPosition)
            typingPosition = -1
            handler.removeCallbacks(typingRunnable)
        }
    }

    fun clearMessages() {
        handler.removeCallbacks(typingRunnable)
        typingPosition = -1
        messages.clear()
        notifyDataSetChanged()
    }

    fun getMessages(): List<ChatMessage> = messages.toList()
}