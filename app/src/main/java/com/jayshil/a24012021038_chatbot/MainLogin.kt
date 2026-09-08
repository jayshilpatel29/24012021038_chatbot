package com.jayshil.a24012021038_chatbot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        // If already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()

        setContentView(R.layout.activity_main_login)

        // IMPORTANT:
        // Your activity_main_login.xml must have a View with id "main"
        val mainView = findViewById<android.view.View>(R.id.main)

        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->

                val systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars())

                v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
                )

                insets
            }
        }

        val email = findViewById<TextInputEditText>(R.id.etEmail)
        val password = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()

            // Check email
            if (emailText.isEmpty()) {
                email.error = "Please enter your email"
                email.requestFocus()
                return@setOnClickListener
            }

            // Check password
            if (passwordText.isEmpty()) {
                password.error = "Please enter your password"
                password.requestFocus()
                return@setOnClickListener
            }

            // LOCAL LOGIN
            if (emailText == "admin@gmail.com" &&
                passwordText == "123456"
            ) {

                // Save login session
                sessionManager.saveToken("local_user_token")

                Toast.makeText(
                    this,
                    "Login successful!",
                    Toast.LENGTH_SHORT
                ).show()

                // Open MainActivity
                startActivity(
                    Intent(this, MainActivity::class.java)
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}