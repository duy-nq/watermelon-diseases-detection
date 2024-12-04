package com.example.diseasesdetection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class InformationFragment : Fragment() {
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var buttonLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.userNameText)
        email = view.findViewById(R.id.userEmailText)
        buttonLogout = view.findViewById(R.id.logoutButton)

        loadData()

        buttonLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadData() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", "")
        val savedEmail = sharedPreferences.getString("email", "")

        username.text = savedUsername
        email.text = savedEmail
    }

    private fun logout() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
        sharedPreferences.edit().remove("username").apply()
        sharedPreferences.edit().remove("email").apply()
        sharedPreferences.edit().remove("uid").apply()

        FirebaseAuth.getInstance().signOut()
        requireActivity().finish()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }

}