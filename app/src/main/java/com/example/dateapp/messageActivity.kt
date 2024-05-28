package com.example.dateapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class messageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
    }


    fun send (view: View)
    {
        Toast.makeText(this, "mesaj gönderildi", Toast.LENGTH_SHORT).show()
    }

    fun messageBack(view: View)
    {
        intent = Intent(applicationContext,rightSwipeActivity::class.java)
        startActivity(intent)
        finish()
            }
}