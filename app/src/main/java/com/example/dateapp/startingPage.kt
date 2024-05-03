package com.example.dateapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class startingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting_page)
    }


    fun logInButton(view: View)
    {

        val intent = Intent(this,logIn::class.java)
        startActivity(intent)

    }

    fun signUpButton(view: View)
    {

        val intent = Intent(this,signUp::class.java)
        startActivity(intent)

    }

}