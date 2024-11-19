package com.example.diseasesdetection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var buttonRegister: Button
    private lateinit var auth: FirebaseAuth

    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var confirmPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        username = findViewById(R.id.registerUsernameInput)
        email = findViewById(R.id.registerEmailInput)
        password = findViewById(R.id.registerPasswordInput)
        confirmPassword = findViewById(R.id.registerConfirmPasswordInput)

        buttonRegister = findViewById(R.id.registerButton)
        buttonRegister.setOnClickListener {
            val inputEmail = email.text.toString()
            val inputPassword = password.text.toString()
            val inputConfirmPassword = confirmPassword.text.toString()

            if (inputEmail.isEmpty() || inputPassword.isEmpty() || inputConfirmPassword.isEmpty()) {
                Toast.makeText(this, R.string.error_empty_field, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (inputPassword != inputConfirmPassword) {
                Toast.makeText(this, R.string.error_password_mismatch, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(inputEmail, inputPassword, username.text.toString())
        }
    }

    private fun register(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    val userId = it.uid
                    val database = FirebaseDatabase.getInstance().getReference("Users")
                    val userMap = mapOf("username" to username, "email" to email)

                    database.child(userId).setValue(userMap).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show()
                            Log.d("RegisterActivity", "User registered successfully")
                            startActivity(Intent(this, LoginActivity::class.java))
                        } else {
                            Log.e("Register", "Error writing user data: ${dbTask.exception?.message}")
                            Toast.makeText(this, "${R.string.error_register_failed}: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this@RegisterActivity, "${R.string.error_unexpected}: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}