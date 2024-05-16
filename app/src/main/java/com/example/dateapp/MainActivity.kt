package com.example.dateapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dateapp.MyAdapter
import com.example.dateapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MyAdapter
    private lateinit var swipeView: SwipeFlingAdapterView
    private lateinit var imageUrls: MutableList<String>
    private lateinit var database : FirebaseFirestore
    private lateinit var currentUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeView = findViewById(R.id.frame)
        imageUrls = mutableListOf()
        database = FirebaseFirestore.getInstance()

        // Current user'ı al
        currentUser = intent.getStringExtra("currentUser") ?: ""

        // Verileri çek
        verileriAl()

        swipeView.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun removeFirstObjectInAdapter() {
                // Kaydırma sonrası ilk elemanı kaldır
                imageUrls.removeAt(0)
                adapter.notifyDataSetChanged() // Adapter'ı güncelle
            }

            override fun onLeftCardExit(dataObject: Any) {
                // Sol kaydırma işlemi
                println("Left Swipe")
            }

            override fun onRightCardExit(dataObject: Any) {
                // Sağ kaydırma işlemi
                println("Right Swipe")
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Adapter'da eleman kalmayınca tekrar yükleme yapılabilir.
            }

            override fun onScroll(scrollProgressPercent: Float) {
                // Kaydırma sırasında yapılacak işlemler
            }
        })
    }

    private fun verileriAl() {
        database.collection("post").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                if (snapshot != null && !snapshot.isEmpty) {
                    val documents = snapshot.documents
                    imageUrls.clear()
                    for (document in documents) {
                        val gorselUrl = document.get("gorselurl") as String
                        imageUrls.add(gorselUrl)
                    }
                    // Veriler yüklendikten sonra adapter yaratılıp set ediliyor.
                    adapter = MyAdapter(this@MainActivity, imageUrls)
                    swipeView.adapter = adapter
                }
            }
        }
    }
}
