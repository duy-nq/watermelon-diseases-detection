package com.example.diseasesdetection

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ForgetActivity : AppCompatActivity() {
    private lateinit var email: TextView
    private lateinit var code: TextView
    private lateinit var password: TextView
    private lateinit var confirmPassword: TextView
    private lateinit var buttonSend: Button
    private lateinit var buttonDone: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        buttonSend.setOnClickListener {
            val inputEmail = email.text.toString()
            if (inputEmail.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_field, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }
}