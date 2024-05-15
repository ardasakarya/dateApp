package com.example.dateapp


import android.app.LauncherActivity
import android.content.Intent
import android.net.wifi.WifiConfiguration.AuthAlgorithm.strings
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PersonalCharacteristics : AppCompatActivity() {
    private lateinit var database: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_characteristics)



        val yasEditText: EditText = findViewById(R.id.yasEditText)
        val sehirSpinner: Spinner= findViewById(R.id.sehirSpinner)
        val boyEditText: EditText = findViewById(R.id.boyEditText)
        val erkekRadioButton: RadioButton = findViewById(R.id.erkekRadioButton)
        val saveButton: Button = findViewById(R.id.saveButton)
        val hobiSpinner: Spinner= findViewById(R.id.hobiSpinner)
        saveButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val hobi = hobiSpinner.selectedItem.toString()
            val yas = yasEditText.text.toString()
            val sehir = sehirSpinner.selectedItem.toString()
            val boy = boyEditText.text.toString()

            val cinsiyet = if (erkekRadioButton.isChecked) "Erkek" else "Kadın"

            val user = hashMapOf(
                "yaş" to yas,
               "şehir" to sehir,
                "boy" to boy,
                "hobi" to hobi,
                "cinsiyet" to cinsiyet
            )

            db.collection("users").add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                    intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error adding document", e)
                }
        }
    }
}

