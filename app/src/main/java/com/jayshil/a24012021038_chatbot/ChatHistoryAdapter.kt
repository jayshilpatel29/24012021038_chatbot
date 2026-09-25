package com.jayshil.a24012021038_chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class ChatHistoryAdapter(
    private val histories: MutableList<ChatHistory>,
    private val onChatClick: (ChatHistory) -> Unit,
    private val onChatDelete: ((ChatHistory) -> Unit)? = null,
    private val onChatRename: ((ChatHistory, String) -> Unit)? = null,
    private val onChatPinToggle: ((ChatHistory) -> Unit)? = null
) : RecyclerView.Adapter<ChatHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val historyCard: MaterialCardView = itemView.findViewById(R.id.historyCard)
        val title: TextView = itemView.findViewById(R.id.tvHistoryTitle)
        val menu: ImageView = itemView.findViewById(R.id.btnHistoryMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat_history,
            parent,
            false
        )
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val chat = histories[position]

        holder.title.text = if (chat.isPinned) {
            "📌 ${chat.title}"
        } else {
            chat.title
        }

        val clickListener = View.OnClickListener {
            onChatClick(chat)
        }

        holder.itemView.setOnClickListener(clickListener)
        holder.historyCard.setOnClickListener(clickListener)

        holder.menu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.menu)

            popup.menu.add(if (chat.isPinned) "Unpin" else "Pin")
            popup.menu.add("Rename")
            popup.menu.add("Delete")

            popup.setOnMenuItemClickListener { item ->
                when (item.title.toString()) {
                    "Pin" -> {
                        chat.isPinned = true
                        onChatPinToggle?.invoke(chat)
                        sort()
                    }

                    "Unpin" -> {
                        chat.isPinned = false
                        onChatPinToggle?.invoke(chat)
                        sort()
                    }

                    "Rename" -> {
                        showRenameDialog(holder, chat)
                    }

                    "Delete" -> {
                        val chatToDelete = chat
                        histories.remove(chatToDelete)
                        notifyDataSetChanged()
                        onChatDelete?.invoke(chatToDelete)
                    }
                }
                true
            }

            popup.show()
        }
    }

    private fun showRenameDialog(holder: HistoryViewHolder, chat: ChatHistory) {
        val editText = EditText(holder.itemView.context)
        editText.setText(chat.title)

        AlertDialog.Builder(holder.itemView.context)
            .setTitle("Rename Chat")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString().trim()
                if (newName.isNotEmpty()) {
                    chat.title = newName
                    notifyDataSetChanged()
                    onChatRename?.invoke(chat, newName)
                }
            }
            .show()
    }

    fun sort() {
        histories.sortWith(compareByDescending<ChatHistory> { it.isPinned })
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = histories.size
}