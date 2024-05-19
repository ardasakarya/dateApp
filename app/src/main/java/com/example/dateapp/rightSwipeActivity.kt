package com.example.dateapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class rightSwipeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RightSwipeAdapter
    private val rightSwipeList = mutableListOf<ImageData>()
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_right_swipe)

        recyclerView = findViewById(R.id.rightSwipeRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RightSwipeAdapter(rightSwipeList)
        recyclerView.adapter = adapter

        currentUser = intent.getStringExtra("currentUser") ?: ""

        // Intent ile gelen veriyi al
        val imageUrl = intent.getStringExtra("imageUrl")
        val email = intent.getStringExtra("email")
        val fullName = intent.getStringExtra("fullName")

        // Veriyi listeye ekle ve adapteri güncelle
        if (imageUrl != null && email != null && fullName != null) {
            rightSwipeList.add(ImageData(imageUrl, email, fullName))
            adapter.notifyDataSetChanged()
        }
    }

    fun back(view: View)
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
