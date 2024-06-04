// messageActivity
package com.example.dateapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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
        val currentUserUID = auth.currentUser?.uid ?: ""
        adapter = MessageAdapter(messages, currentUserUID)

        messageRecycler.layoutManager = LinearLayoutManager(this)
        messageRecycler.adapter = adapter

        val receiverUID = intent.getStringExtra("receiverUID")
        Log.d("MessageActivity", "Receiver User UID: $receiverUID")

        if (currentUserUID.isNotEmpty() && receiverUID != null) {
            val chatId = getChatId(currentUserUID, receiverUID)
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

        sendButton.setOnClickListener {
            messageSend(receiverUID)
        }
    }

    private fun messageSend(receiverUID: String?) {
        val currentUserUID = auth.currentUser?.uid ?: ""
        val text = messageEditText.text.toString()
        if (text.isNotEmpty() && receiverUID != null) {
            val chatId = getChatId(currentUserUID, receiverUID)
            val message = Message(text, currentUserUID, receiverUID, System.currentTimeMillis())
            db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener {
                    messageEditText.text.clear()
                    Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("MessageActivity", "Error sending message", e)
                }
        }
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) {
            "$uid1-$uid2"
        } else {
            "$uid2-$uid1"
        }
    }
}
