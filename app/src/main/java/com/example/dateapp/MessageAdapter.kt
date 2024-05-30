package com.example.dateapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

data class Message(
    val text: String,
    val senderEmail: String,
    val timestamp: Long
)

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ME = 1
    private val VIEW_TYPE_YOU = 2

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderEmail == FirebaseAuth.getInstance().currentUser?.email) {
            VIEW_TYPE_ME
        } else {
            VIEW_TYPE_YOU
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ME) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_side_me, parent, false)
            MeViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message_side_you, parent, false)
            YouViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is MeViewHolder) {
            holder.bind(message)
        } else if (holder is YouViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    inner class MeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val meTextView: TextView = itemView.findViewById(R.id.meTextView)
        fun bind(message: Message) {
            meTextView.text = message.text
        }
    }

    inner class YouViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val youTextView: TextView = itemView.findViewById(R.id.youTextView)
        fun bind(message: Message) {
            youTextView.text = message.text
        }
    }
}
