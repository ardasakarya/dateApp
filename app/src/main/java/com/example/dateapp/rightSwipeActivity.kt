// rightSwipeActivity
package com.example.dateapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class rightSwipeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RightSwipeAdapter
    private val likedUsersList = mutableListOf<LikedUserData>()
    private lateinit var database: FirebaseFirestore
    private lateinit var currentUserEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_right_swipe)

        recyclerView = findViewById(R.id.rightSwipeRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RightSwipeAdapter(likedUsersList)
        recyclerView.adapter = adapter

        database = FirebaseFirestore.getInstance()
        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""

        fetchLikedUsers()
    }

    fun back(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun mesajGit(view: View) {
        val position = recyclerView.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION) {
            val likedUser = likedUsersList[position]
            val receiverUID = likedUser.likerUid

            val intent = Intent(this, messageActivity::class.java)
            intent.putExtra("receiverUID", receiverUID)
            startActivity(intent)
        }
    }

    private fun fetchLikedUsers() {
        database.collection("likes")
            .whereEqualTo("likedEmail", currentUserEmail)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("FirestoreError", "Error fetching data", exception)
                } else {
                    if (snapshot != null && !snapshot.isEmpty) {
                        val documents = snapshot.documents
                        likedUsersList.clear()
                        for (document in documents) {
                            val likerEmail = document.getString("likerEmail") ?: ""
                            val likerFullName = document.getString("likerFullName") ?: ""
                            val likerImageUrl = document.getString("likerImageUrl") ?: ""
                            val likerUid = document.getString("likerUid") ?: ""
                            val likedEmail = document.getString("likedEmail") ?: ""
                            val likedUserData = LikedUserData(
                                likerEmail, likerFullName, likerImageUrl, likedEmail, likerUid
                            )
                            likedUsersList.add(likedUserData)
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.d("Firestore", "No data found")
                    }
                }
            }
    }
}
