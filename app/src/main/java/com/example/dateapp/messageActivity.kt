package com.example.dateapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class messageActivity : AppCompatActivity() {

    private lateinit var messageRecycler: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var adapter: MessageAdapter
    private lateinit var messages: MutableList<Message>
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        messageRecycler = findViewById(R.id.messageRecycler)
        messageEditText = findViewById(R.id.messageText)


        messages = mutableListOf()
        adapter = MessageAdapter(messages)

        messageRecycler.layoutManager = LinearLayoutManager(this)
        messageRecycler.adapter = adapter

        val currentUserEmail = auth.currentUser?.email
        val otherUserEmail = intent.getStringExtra("otherUserEmail")

        if (currentUserEmail != null && otherUserEmail != null) {
            val chatRef = db.collection("chats")
                .document("$currentUserEmail-$otherUserEmail")
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)

            chatRef.addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener

                messages.clear()
                for (doc in snapshot.documents) {
                    val message = doc.toObject(Message::class.java)
                    if (message != null) messages.add(message)
                }
                adapter.notifyDataSetChanged()
            }


        }
    }


    fun messageSend(view: View) {
        val currentUserEmail = auth.currentUser?.email
        val otherUserEmail = intent.getStringExtra("otherUserEmail")
        val text = messageEditText.text.toString()
        if (text.isNotEmpty()) {
            val message = Message(text, currentUserEmail!!, System.currentTimeMillis())
            db.collection("chats")
                .document("$currentUserEmail-$otherUserEmail")
                .collection("messages")
                .add(message)
            messageEditText.text.clear()
        }
    }

}
