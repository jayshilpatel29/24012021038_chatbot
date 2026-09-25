package com.jayshil.a24012021038_chatbot

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
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
import com.jayshil.a24012021038_chatbot.api.NexusClient
import java.net.URL

class MainLogin : AppCompatActivity() {

    private val BASE_URL get() = NexusClient.getBaseUrl().removeSuffix("/")

    private lateinit var sessionManager: SessionManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // If already logged in, go directly to MainActivity
        if (sessionManager.isLoggedIn()) {
            goToMainActivity()
            return
        }

        enableEdgeToEdge()

        setContentView(R.layout.activity_main_login)

        val mainView = findViewById<View>(R.id.main)

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

        val email = findViewById<TextInputEditText>(R.id.etEmail)
        val password = findViewById<TextInputEditText>(R.id.etPassword)

        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnGoogle = findViewById<MaterialButton>(R.id.btnGoogle)
        val btnSignup = findViewById<MaterialButton>(R.id.btnSignup)

        // LOGIN
        btnLogin.setOnClickListener {

            val emailText = email.text?.toString()?.trim() ?: ""
            val passwordText = password.text?.toString() ?: ""

            // Validate email
            if (emailText.isEmpty()) {
                email.error = "Please enter your email"
                email.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Please enter a valid email"
                email.requestFocus()
                return@setOnClickListener
            }

            // Validate password
            if (passwordText.isEmpty()) {
                password.error = "Please enter your password"
                password.requestFocus()
                return@setOnClickListener
            }

            loginUser(
                emailText,
                passwordText,
                btnLogin
            )
        }

        // GOOGLE LOGIN
        btnGoogle.setOnClickListener {

            Toast.makeText(
                this,
                "Google Login will be connected next",
                Toast.LENGTH_SHORT
            ).show()
        }

        // SIGN UP
        btnSignup.setOnClickListener {

            val intent = Intent(
                this,
                SignUpActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun loginUser(
        email: String,
        password: String,
        button: MaterialButton
    ) {

        // Prevent multiple clicks
        button.isEnabled = false

        lifecycleScope.launch {

            try {

                val result = withContext(Dispatchers.IO) {

                    val url = URL(
                        "$BASE_URL/auth/login"
                    )

                    val connection =
                        url.openConnection() as HttpURLConnection

                    connection.requestMethod = "POST"

                    connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )

                    connection.setRequestProperty(
                        "Accept",
                        "application/json"
                    )

                    connection.connectTimeout = 10000
                    connection.readTimeout = 15000
                    connection.doOutput = true

                    // Create JSON
                    val json = JSONObject()

                    json.put(
                        "email",
                        email
                    )

                    json.put(
                        "password",
                        password
                    )

                    // Send request
                    connection.outputStream.use { output ->

                        output.write(
                            json.toString()
                                .toByteArray(Charsets.UTF_8)
                        )
                    }

                    val responseCode =
                        connection.responseCode

                    val responseText = if (
                        responseCode in 200..299
                    ) {

                        connection.inputStream
                            .bufferedReader()
                            .use { reader ->
                                reader.readText()
                            }

                    } else {

                        connection.errorStream
                            ?.bufferedReader()
                            ?.use { reader ->
                                reader.readText()
                            }
                            ?: "Login failed"
                    }

                    connection.disconnect()

                    // Backend error
                    if (responseCode !in 200..299) {

                        var errorMessage =
                            "Login failed ($responseCode)"

                        try {

                            val errorJson =
                                JSONObject(responseText)

                            errorMessage =
                                errorJson.optString(
                                    "detail",
                                    errorMessage
                                )

                        } catch (_: Exception) {

                            if (responseText.isNotEmpty()) {
                                errorMessage = responseText
                            }
                        }

                        throw Exception(errorMessage)
                    }

                    // Convert response into JSONObject
                    JSONObject(responseText)
                }

                // Get access token
                val token = result.optString(
                    "access_token",
                    ""
                )

                // Get user object
                val userObject =
                    result.optJSONObject("user")

                val userName =
                    userObject?.optString(
                        "name",
                        ""
                    ) ?: ""

                val userEmail =
                    userObject?.optString(
                        "email",
                        email
                    ) ?: email

                // Check token
                if (token.isEmpty()) {

                    Toast.makeText(
                        this@MainLogin,
                        "Login failed: Token not received",
                        Toast.LENGTH_LONG
                    ).show()

                    button.isEnabled = true
                    return@launch
                }

                // Save login session
                sessionManager.saveToken(token)

                // Save user's name and email
                sessionManager.saveUser(
                    userName,
                    userEmail
                )

                Toast.makeText(
                    this@MainLogin,
                    "Login successful!",
                    Toast.LENGTH_SHORT
                ).show()

                // Open main screen
                goToMainActivity()

            } catch (e: Exception) {

                Toast.makeText(
                    this@MainLogin,
                    "Login Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                button.isEnabled = true
            }
        }
    }

    private fun goToMainActivity() {

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}