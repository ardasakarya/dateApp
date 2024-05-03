package com.example.dateapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class logIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        auth = FirebaseAuth.getInstance()
        val guncelKullanici = auth.currentUser
    }






    fun girisYap(view: View) {

        val epostaEditText: TextView = findViewById(R.id.epostaEditText)
        val sifreEditText: TextView = findViewById(R.id.sifreEditText)

        auth.signInWithEmailAndPassword(
            epostaEditText.text.toString(),
            sifreEditText.text.toString()
        )
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val guncelKullanici = auth.currentUser?.email.toString()
                    Toast.makeText(this, "Hoşgeldin:${guncelKullanici}", Toast.LENGTH_LONG)
                        .show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }

}