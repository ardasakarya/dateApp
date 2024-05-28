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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lorentzos.flingswipe.SwipeFlingAdapterView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private lateinit var swipeView: SwipeFlingAdapterView
    private lateinit var imageList: MutableList<ImageData>
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeView = findViewById(R.id.frame)
        imageList = mutableListOf()
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUserEmail = auth.currentUser?.email

        if (currentUserEmail != null) {
            verileriAl()

            swipeView.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
                override fun removeFirstObjectInAdapter() {
                    imageList.removeAt(0)
                    adapter.notifyDataSetChanged()
                }

                override fun onLeftCardExit(dataObject: Any) {
                    Toast.makeText(applicationContext, "left swipe", Toast.LENGTH_SHORT).show()
                }

                override fun onRightCardExit(dataObject: Any) {
                    if (dataObject is ImageData) {
                        val likerEmail = currentUserEmail
                        val likedEmail = dataObject.email

                        database.collection("post")
                            .whereEqualTo("email", likerEmail)
                            .get()
                            .addOnSuccessListener { documents ->
                                if (!documents.isEmpty) {
                                    val userDocument = documents.first()
                                    val likerFullName = userDocument.getString("fullName") ?: ""
                                    val likerImageUrl = userDocument.getString("gorselurl") ?: ""

                                    val likedUserData = hashMapOf(
                                        "likerEmail" to likerEmail,
                                        "likerFullName" to likerFullName,
                                        "likerImageUrl" to likerImageUrl,
                                        "likedEmail" to likedEmail
                                    )

                                    database.collection("likes").add(likedUserData)
                                        .addOnSuccessListener {
                                            Log.d("FirestoreSuccess", "Right swipe data added successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("FirestoreError", "Error adding right swipe data", e)
                                        }

                                    Toast.makeText(applicationContext, "right swipe", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }

                override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {}

                override fun onScroll(scrollProgressPercent: Float) {}
            })
        }

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
                    val intent = Intent(this, rightSwipeActivity::class.java)
                    intent.putExtra("currentUserEmail", auth.currentUser?.email)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.logout -> {
                    auth.signOut()
                    Toast.makeText(this, "Logout seçildi", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}
