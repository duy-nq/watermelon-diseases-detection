package com.example.diseasesdetection

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var appLogo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        appLogo = findViewById(R.id.logo)
        appLogo.startAnimation(fadeIn)

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        registerLink = findViewById(R.id.registerText)
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}