package com.example.diseasesdetection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var appLogo: ImageView

    private lateinit var auth: FirebaseAuth

    private lateinit var username: TextView
    private lateinit var password: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else {
            setContentView(R.layout.activity_login)
        }

        auth = FirebaseAuth.getInstance()

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        username = findViewById(R.id.usernameInput)
        password = findViewById(R.id.passwordInput)

        appLogo = findViewById(R.id.logo)
        appLogo.startAnimation(fadeIn)

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            if (username.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_field, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(username.text.toString(), password.text.toString())
        }

        registerLink = findViewById(R.id.registerText)
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login(email: String, password: String) {
        // Show loading spinner if you have one
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Login successful
                val user = auth.currentUser
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                user?.let {
                    Log.d("LoginActivity", "Login successful for user: ${it.email}")

                    // Optionally fetch user details from the database
                    val userId = it.uid
                    val database = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    database.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val username = snapshot.child("username").value.toString()

                            Log.d("LoginActivity", "Welcome back, $username!")
                            Toast.makeText(this, "Welcome back, $username!", Toast.LENGTH_SHORT).show()

                            sharedPreferences.edit().putString("username", username).apply()
                            sharedPreferences.edit().putString("email", email).apply()
                            sharedPreferences.edit().putString("uid", userId).apply()
                            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                            startActivity(Intent(this, MainActivity::class.java))
                        } else {
                            Log.w("LoginActivity", "User data not found in database")
                            Toast.makeText(this, R.string.error_empty_field, Toast.LENGTH_SHORT).show()
                        }

                        // Navigate to the main screen
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }.addOnFailureListener { error ->
                        Log.e("LoginActivity", "Error fetching user data: ${error.message}")
                        Toast.makeText(this, R.string.error_unexpected, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val errorMessage = when (task.exception) {
                    is FirebaseAuthInvalidUserException -> "No account found with this email."
                    is FirebaseAuthInvalidCredentialsException -> "Invalid email or password."
                    else -> "Unexpected error: ${task.exception?.message}"
                }
                Log.e("LoginActivity", errorMessage)
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

}