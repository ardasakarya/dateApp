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



    fun back(view:View)
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun mesajGit(view: View) {
        // Firestore veritabanına erişim sağla
        val db = FirebaseFirestore.getInstance()

        // Veritabanından likedEmail değerini al
        db.collection("likes").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val likerEmail = document.getString("likerEmail") ?: ""

                    // Veriyi başka bir sayfaya göndermek için Intent oluştur
                    val intent = Intent(applicationContext, messageActivity::class.java)
                    intent.putExtra("likerEmail", likerEmail)
                    startActivity(intent)
                    return@addOnSuccessListener  // İlk belge bulunduğunda işlemi sonlandır
                }
                Log.d(TAG, "Belge bulunamadı.")
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Veri alınamadı: ", exception)
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
                            val likedEmail = document.getString("likedEmail") ?: ""
                            val likedUserData = LikedUserData(
                                likerEmail, likerFullName, likerImageUrl, likedEmail
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
