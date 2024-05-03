package com.example.dateapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.TextView

class signUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        val guncelKullanici = auth.currentUser
    }


    fun kayitOl(view: View) {
        val EmailText: TextView = findViewById(R.id.signUpEpostaEditText)
        val PasswordText: TextView = findViewById(R.id.signUpSifreEditText)
        val email = EmailText.text.toString()
        val sifre = PasswordText.text.toString()
        auth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, PersonalCharacteristics::class.java)
                startActivity(intent)
                finish()
            }
        }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    exception.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}