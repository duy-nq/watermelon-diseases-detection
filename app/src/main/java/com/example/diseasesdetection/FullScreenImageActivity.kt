package com.example.diseasesdetection

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        val imageView = findViewById<ImageView>(R.id.fullscreenImageView)
        val imageUri = intent.getParcelableExtra<Uri>("imageUri")
        imageView.setImageURI(imageUri)

        imageView.setOnClickListener {
            finish() // Close activity on click
        }
    }
}