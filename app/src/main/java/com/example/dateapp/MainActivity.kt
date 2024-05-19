package com.example.dateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
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
                Toast.makeText(applicationContext, "left swipe", Toast.LENGTH_SHORT).show()


//email, fotoğraf, fullname verilerini gönderip adapter döngüsünü başlat.


            }

            override fun onRightCardExit(dataObject: Any) {
                Toast.makeText(applicationContext, "right swipe", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}

            override fun onScroll(scrollProgressPercent: Float) {}
        })

        val menuButton: ImageView = findViewById(R.id.menuButton)
        menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
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
                        val fullName = document.getString("fullName") ?: ""
                        Log.d("FirestoreData", "Fetched document: $document")
                        if (gorselUrl.isNotEmpty() && email.isNotEmpty() && fullName.isNotEmpty()) {
                            val imageData = ImageData(gorselUrl, email, fullName)
                            imageList.add(imageData)
                            Log.d("FirestoreSuccess", "Added: $gorselUrl, $email, $fullName")
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
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.begenenler -> {
                    // "Beğenenler" seçeneği tıklandığında yapılacak işlemler
                    val intent = Intent(this, rightSwipeActivity::class.java)
                    startActivity(intent)
                     finish()
                    true
                }
                R.id.logout -> {
                    // "Logout" seçeneği tıklandığında yapılacak işlemler
                    Toast.makeText(this, "Logout seçildi", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}




