package com.jayshil.a24012021038_chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ChatAdapter(
    private val messages: MutableList<ChatMessage>
) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val messageCard: MaterialCardView =
            itemView.findViewById(R.id.messageCard)

        val tvMessage: TextView =
            itemView.findViewById(R.id.tvMessage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ) {

        val message = messages[position]

        holder.tvMessage.text = message.message

        val params = holder.messageCard.layoutParams
                as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams

        if (message.isUser) {

            // USER MESSAGE → RIGHT SIDE

            params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            params.startToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET

            params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            params.endToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET

            holder.messageCard.layoutParams = params

            holder.messageCard.setCardBackgroundColor(
                android.graphics.Color.parseColor("#F0444D")
            )

            holder.tvMessage.setTextColor(
                android.graphics.Color.WHITE
            )

        } else {

            // AI MESSAGE → LEFT SIDE

            params.endToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET
            params.endToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET

            params.startToStart = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
            params.startToEnd = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET

            holder.messageCard.layoutParams = params

            holder.messageCard.setCardBackgroundColor(
                android.graphics.Color.parseColor("#F4F4F4")
            )

            holder.tvMessage.setTextColor(
                android.graphics.Color.parseColor("#222222")
            )
        }
    }
    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}