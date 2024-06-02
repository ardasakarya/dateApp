package com.example.dateapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class messageActivity : AppCompatActivity() {

    private lateinit var messageRecycler: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var adapter: MessageAdapter
    private lateinit var messages: MutableList<Message>
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        messageRecycler = findViewById(R.id.messageRecycler)
        messageEditText = findViewById(R.id.messageText)
        sendButton = findViewById(R.id.sendButton)

        messages = mutableListOf()
        val currentUserEmail = auth.currentUser?.email ?: ""
        adapter = MessageAdapter(messages, currentUserEmail)

        messageRecycler.layoutManager = LinearLayoutManager(this)
        messageRecycler.adapter = adapter

        val otherUserEmail = intent.getStringExtra("likerEmail")
        Log.d("MessageActivity", "Other User Email: $otherUserEmail")

        if (currentUserEmail.isNotEmpty() && otherUserEmail != null) {
            val chatId = getChatId(currentUserEmail, otherUserEmail)
            val chatRef = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)

            chatRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("MessageActivity", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val message = doc.toObject(Message::class.java)
                        if (message != null) {
                            messages.add(message)
                        }
                    }
                    adapter.notifyDataSetChanged()
                    messageRecycler.scrollToPosition(messages.size - 1)
                }
            }
        }
    }

    fun messageSend(view: View) {
        val currentUserEmail = auth.currentUser?.email ?: ""
        val otherUserEmail = intent.getStringExtra("likerEmail")
        Log.d("MessageActivity", "Sending message to: $otherUserEmail")

        val text = messageEditText.text.toString()
        if (text.isNotEmpty() && otherUserEmail != null) {
            val chatId = getChatId(currentUserEmail, otherUserEmail)
            val message = Message(text, currentUserEmail, otherUserEmail, System.currentTimeMillis())
            db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    messageEditText.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.w("MessageActivity", "Error adding message", e)
                }
        }
    }

    private fun getChatId(user1: String, user2: String): String {
        return if (user1 < user2) {
            "$user1-$user2"
        } else {
            "$user2-$user1"
        }
    }
}
