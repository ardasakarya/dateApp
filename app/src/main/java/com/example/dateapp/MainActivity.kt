package com.example.dateapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.lorentzos.flingswipe.SwipeFlingAdapterView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private lateinit var swipeView: SwipeFlingAdapterView
    private lateinit var imageList: MutableList<ImageData>
    private lateinit var database: FirebaseFirestore
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeView = findViewById(R.id.frame)
        imageList = mutableListOf()
        database = FirebaseFirestore.getInstance()

        currentUser = intent.getStringExtra("currentUser") ?: ""

        verileriAl()

        swipeView.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                imageList.removeAt(0)
                adapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                println("Left Swipe")
            }

            override fun onRightCardExit(dataObject: Any) {
                println("Right Swipe")
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}

            override fun onScroll(scrollProgressPercent: Float) {}
        })
    }

    private fun verileriAl() {
        database.collection("post").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                Log.e("FirestoreError", "Error fetching data", exception)
            } else {
                if (snapshot != null && !snapshot.isEmpty) {
                    val documents = snapshot.documents
                    imageList.clear()
                    for (document in documents) {
                        val gorselUrl = document.getString("gorselurl") ?: ""
                        val email = document.getString("email") ?: ""
                        Log.d("FirestoreData", "Fetched document: $document")
                        if (gorselUrl.isNotEmpty() && email.isNotEmpty()) {
                            val imageData = ImageData(gorselUrl, email)
                            imageList.add(imageData)
                            Log.d("FirestoreSuccess", "Added: $gorselUrl, $email")
                        } else {
                            Log.e("FirestoreError", "Invalid data: $document")
                        }
                    }
                    adapter = MyAdapter(this@MainActivity, imageList)
                    swipeView.adapter = adapter
                } else {
                    Log.d("Firestore", "No data found")
                }
            }
        }
    }
}
