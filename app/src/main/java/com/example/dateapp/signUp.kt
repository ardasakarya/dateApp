package com.example.dateapp

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class signUp : AppCompatActivity() {
    var secilenGorsel : Uri? = null
    var secilenBitmap : Bitmap? = null
    private lateinit var storage : FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val guncelKullanici = auth.currentUser
    }


    fun kayitOl(view: View) {

        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.jpg"

        val reference = storage.reference
        val gorselReference = reference.child("images").child(gorselIsmi)
        if (secilenGorsel != null) {
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener { TaskSnapshot ->

                val yuklenenGorselReference =
                    FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val tarih = com.google.firebase.Timestamp.now()

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("gorselurl", downloadUrl)
                    postHashMap.put("tarih", tarih)

                    database.collection("post").add(postHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
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
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                applicationContext,
                                exception.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()

                        }
                }
            }


        }

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


    fun gorselSec(view: View)
    {

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf( android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else
        {
            val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1)
        {
            if (grantResults.size> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                val galeriIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val signUpImageView: ImageButton = findViewById(R.id.signUpImageView)

        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null)
        {
            secilenGorsel = data.data

            if(secilenGorsel != null)
            {
                if (Build.VERSION.SDK_INT >=28)
                {
                    val source = ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    signUpImageView.setImageBitmap(secilenBitmap)
                }
                else {
                    secilenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    signUpImageView.setImageBitmap(secilenBitmap)
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }



}