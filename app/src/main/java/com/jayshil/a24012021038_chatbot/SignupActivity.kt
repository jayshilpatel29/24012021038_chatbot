package com.jayshil.a24012021038_chatbot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8000"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        val mainView = findViewById<android.view.View>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val name = findViewById<TextInputEditText>(R.id.etName)
        val email = findViewById<TextInputEditText>(R.id.etEmail)
        val password = findViewById<TextInputEditText>(R.id.etPassword)
        val confirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<MaterialButton>(R.id.btnSignUp)
        val btnGoogle = findViewById<MaterialButton>(R.id.btnGoogle)

        btnSignUp.setOnClickListener {
            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()

            if (nameText.isEmpty()) {
                name.error = "Please enter your name"
                name.requestFocus()
                return@setOnClickListener
            }

            if (emailText.isEmpty()) {
                email.error = "Please enter your email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Please enter a valid email address"
                email.requestFocus()
                return@setOnClickListener
            }

            if (passwordText.isEmpty()) {
                password.error = "Please enter your password"
                password.requestFocus()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                password.error = "Password must be at least 6 characters"
                password.requestFocus()
                return@setOnClickListener
            }

            if (confirmPasswordText.isEmpty()) {
                confirmPassword.error = "Please confirm your password"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            if (passwordText != confirmPasswordText) {
                confirmPassword.error = "Passwords do not match"
                confirmPassword.requestFocus()
                return@setOnClickListener
            }

            registerUser(nameText, emailText, passwordText)
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(
                this,
                "Google Sign Up is available via standard authentication",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        password: String
    ) {
        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val url = URL("$BASE_URL/auth/register")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.connectTimeout = 10000
                    connection.readTimeout = 15000
                    connection.doOutput = true

                    val json = JSONObject()
                    json.put("name", name)
                    json.put("email", email)
                    json.put("password", password)

                    connection.outputStream.use { output ->
                        output.write(json.toString().toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    val responseText = if (responseCode in 200..299) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                            ?: "Registration failed"
                    }

                    connection.disconnect()

                    if (responseCode !in 200..299) {
                        var errorMsg = "Registration failed ($responseCode)"
                        try {
                            val errJson = JSONObject(responseText)
                            errorMsg = errJson.optString("detail", errorMsg)
                        } catch (e: Exception) {
                            errorMsg = responseText
                        }
                        throw Exception(errorMsg)
                    }

                    JSONObject(responseText)
                }

                val status = result.optString("status")
                if (status == "success") {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Account created successfully! Please login.",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@SignUpActivity, MainLogin::class.java)
                    intent.putExtra("registered_email", email)
                    startActivity(intent)
                    finish()
                } else {
                    val msg = result.optString("message", "Registration failed")
                    Toast.makeText(
                        this@SignUpActivity,
                        msg,
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@SignUpActivity,
                    "Signup Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}