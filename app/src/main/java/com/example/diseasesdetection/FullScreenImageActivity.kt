package com.example.diseasesdetection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

//        val imageView = findViewById<ImageView>(R.id.fullscreenImageView)
//        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
//        imageView.setImageURI(imageUri)
//
//        imageView.setOnClickListener {
//            finish() // Close activity on click
//        }
    }
}