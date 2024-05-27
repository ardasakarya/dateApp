package com.example.dateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class rightSwipeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RightSwipeAdapter
    private val likedUsersList = mutableListOf<LikedUserData>()
    private lateinit var database: FirebaseFirestore
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_right_swipe)

        recyclerView = findViewById(R.id.rightSwipeRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RightSwipeAdapter(likedUsersList)
        recyclerView.adapter = adapter

        database = FirebaseFirestore.getInstance()
        currentUser = intent.getStringExtra("currentUser") ?: ""

        fetchLikedUsers()
    }
fun back(view: View)
{
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
    finish()
}
    private fun fetchLikedUsers() {
        database.collection("likes")
            .whereEqualTo("likedEmail", currentUser)
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
                            val likedUserData = LikedUserData(
                                likerEmail, likerFullName, likerImageUrl, currentUser
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
