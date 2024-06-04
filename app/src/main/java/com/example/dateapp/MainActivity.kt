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
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var flingContainer: SwipeFlingAdapterView
    private var imageList = ArrayList<ImageData>()
    private var arrayAdapter: MyAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()

        flingContainer = findViewById(R.id.frame)

        verileriAl()

        arrayAdapter = MyAdapter(this, imageList)
        flingContainer.adapter = arrayAdapter

        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                imageList.removeAt(0)
                arrayAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                Toast.makeText(applicationContext, "left swipe", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                if (dataObject is ImageData) {
                    val currentUserEmail = auth.currentUser?.email ?: return
                    val currentUserUID = auth.currentUser?.uid ?: return
                    val receiverUID = dataObject.uid

                    database.collection("post")
                        .whereEqualTo("email", currentUserEmail)
                        .get()
                        .addOnSuccessListener { documents ->
                            if (!documents.isEmpty) {
                                val userDocument = documents.first()
                                val likerFullName = userDocument.getString("fullName") ?: ""
                                val likerImageUrl = userDocument.getString("gorselurl") ?: ""

                                val likedUserData = hashMapOf(
                                    "likerEmail" to currentUserEmail,
                                    "likerFullName" to likerFullName,
                                    "likerImageUrl" to likerImageUrl,
                                    "likerUid" to currentUserUID,
                                    "likedEmail" to dataObject.email,
                                    "likedUid" to receiverUID
                                )

                                database.collection("likes").add(likedUserData)
                                    .addOnSuccessListener {
                                        Log.d("FirestoreSuccess", "Right swipe data added successfully")
                                        val intent = Intent(this@MainActivity, messageActivity::class.java)
                                        intent.putExtra("receiverUID", receiverUID)

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
                        val uid = document.getString("uid") ?: ""
                        Log.d("FirestoreData", "Fetched document: $document")
                        if (gorselUrl.isNotEmpty() && email.isNotEmpty() && fullName.isNotEmpty() && uid.isNotEmpty()) {
                            val imageData = ImageData(gorselUrl, email, fullName, uid)
                            imageList.add(imageData)
                            Log.d("FirestoreSuccess", "Added: $gorselUrl, $email, $fullName, $uid")
                        }
                    }
                    arrayAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun messageBox(view: View) {
        val intent = Intent(this, messageBox::class.java)
        startActivity(intent)
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
