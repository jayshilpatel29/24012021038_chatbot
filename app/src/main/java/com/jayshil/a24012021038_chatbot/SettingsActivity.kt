package com.jayshil.a24012021038_chatbot

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

import com.jayshil.a24012021038_chatbot.api.NexusClient

class SettingsActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    private lateinit var sessionManager: SessionManager

    private val BASE_URL get() = NexusClient.getBaseUrl().removeSuffix("/")

    private lateinit var tvProfileInitials: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvThemeStatus: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)
        preferences = getSharedPreferences("nexus_settings", MODE_PRIVATE)

        val btnBack = findViewById<View>(R.id.btnBack)
        val btnEditProfile = findViewById<MaterialButton>(R.id.btnEditProfile)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchTheme)
        val switchEnter = findViewById<SwitchMaterial>(R.id.switchEnter)
        val switchTimestamps = findViewById<SwitchMaterial>(R.id.switchTimestamps)
        val switchNotifications = findViewById<SwitchMaterial>(R.id.switchNotifications)
        val switchResponse = findViewById<SwitchMaterial>(R.id.switchResponse)
        val switchSaveChat = findViewById<SwitchMaterial>(R.id.switchSaveChat)

        val btnClearCurrentChat = findViewById<View>(R.id.btnClearCurrentChat)
        val btnClearHistory = findViewById<View>(R.id.btnClearHistory)
        val btnDataPrivacy = findViewById<View>(R.id.btnDataPrivacy)
        val btnClearLocalData = findViewById<View>(R.id.btnClearLocalData)
        val btnChangePassword = findViewById<View>(R.id.btnChangePassword)
        val btnLogout = findViewById<View>(R.id.btnLogout)
        val btnDeleteAccount = findViewById<View>(R.id.btnDeleteAccount)
        val btnAbout = findViewById<View>(R.id.btnAbout)
        val btnTerms = findViewById<View>(R.id.btnTerms)
        val btnPrivacyPolicy = findViewById<View>(R.id.btnPrivacyPolicy)

        tvProfileInitials = findViewById(R.id.tvProfileInitials)
        tvProfileName = findViewById(R.id.tvProfileName)
        tvEmail = findViewById(R.id.tvEmail)
        tvThemeStatus = findViewById(R.id.tvThemeStatus)

        // Load saved switch states
        val isDark = preferences.getBoolean("dark_mode", false)
        switchTheme.isChecked = isDark
        tvThemeStatus.text = if (isDark) "Dark" else "Light"

        switchEnter.isChecked = preferences.getBoolean("send_enter", true)
        switchTimestamps.isChecked = preferences.getBoolean("timestamps", false)
        switchNotifications.isChecked = preferences.getBoolean("notifications", true)
        switchResponse.isChecked = preferences.getBoolean("response_completed", true)
        switchSaveChat.isChecked = preferences.getBoolean("save_chat", true)

        // Populate cached user info first
        val savedName =
            sessionManager.getUserName() ?: preferences.getString("username", "Nexus User")
        val savedEmail =
            sessionManager.getUserEmail() ?: preferences.getString("user_email", "user@nexus.ai")
        updateProfileViews(savedName ?: "Nexus User", savedEmail ?: "user@nexus.ai")

        // Fetch fresh profile from backend using JWT
        loadUserProfile()

        btnBack.setOnClickListener { finish() }

        btnEditProfile.setOnClickListener { showEditProfileDialog() }

        switchTheme.setOnCheckedChangeListener { _, checked ->
            preferences.edit().putBoolean("dark_mode", checked).apply()
            tvThemeStatus.text = if (checked) "Dark" else "Light"

            if (checked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        switchEnter.setOnCheckedChangeListener { _, checked ->
            preferences.edit().putBoolean("send_enter", checked).apply()
        }

        switchTimestamps.setOnCheckedChangeListener { _, checked ->
            preferences.edit().putBoolean("timestamps", checked).apply()
        }

        switchNotifications.setOnCheckedChangeListener { _, checked ->
            preferences.edit().putBoolean("notifications", checked).apply()
        }

        switchResponse.setOnCheckedChangeListener { _, checked ->
            preferences.edit().putBoolean("response_completed", checked).apply()
        }

        switchSaveChat.setOnCheckedChangeListener { _, checked ->
            preferences.edit().putBoolean("save_chat", checked).apply()
        }

        btnClearCurrentChat.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear Current Chat?")
                .setMessage("This will remove the current conversation.")
                .setPositiveButton("Clear") { _, _ ->
                    val intent = Intent("com.jayshil.a24012021038_chatbot.CLEAR_CURRENT_CHAT")
                    sendBroadcast(intent)
                    Toast.makeText(this, "Current chat cleared", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnClearHistory.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear Chat History?")
                .setMessage("Are you sure you want to clear all chat history?")
                .setPositiveButton("Clear") { _, _ ->
                    performClearAllChatHistory()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        btnDataPrivacy.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Data & Privacy")
                .setMessage("Your account information is stored securely in the Nexus backend database.\n\nYour AI messages are processed by Gemini via your secure Nexus backend.")
                .setPositiveButton("OK", null)
                .show()
        }

        btnClearLocalData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear Local Data?")
                .setMessage("This will reset your local app settings.")
                .setPositiveButton("Clear") { _, _ ->
                    preferences.edit().clear().apply()
                    Toast.makeText(this, "Local data cleared", Toast.LENGTH_SHORT).show()
                    recreate()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnChangePassword.setOnClickListener { showChangePasswordDialog() }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Are you sure you want to logout from Nexus?")
                .setPositiveButton("Logout") { _, _ ->
                    sessionManager.logout()
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainLogin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnDeleteAccount.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Account?")
                .setMessage("This action cannot be undone. All your profile data and chats will be permanently removed.")
                .setPositiveButton("Delete") { _, _ ->
                    performDeleteAccount()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("About Nexus")
                .setMessage("Nexus AI Chatbot Application\nVersion 1.0.0\nPowered by Google Gemini AI")
                .setPositiveButton("OK", null)
                .show()
        }

        btnTerms.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Terms of Service")
                .setMessage("By using Nexus, you agree to use the application responsibly and adhere to standard safety guidelines.")
                .setPositiveButton("OK", null)
                .show()
        }

        btnPrivacyPolicy.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Privacy Policy")
                .setMessage("Nexus values your privacy. User credentials and passwords are encrypted and hashed in the backend. Gemini API keys remain strictly server-side.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getToken()
                if (token.isNullOrEmpty()) return@launch

                val result = withContext(Dispatchers.IO) {
                    val url = URL("$BASE_URL/auth/me")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.setRequestProperty("Authorization", "Bearer $token")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000

                    val responseCode = connection.responseCode
                    if (responseCode != 200) {
                        throw Exception("HTTP $responseCode")
                    }

                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    connection.disconnect()
                    JSONObject(response)
                }

                val user = result.optJSONObject("user")
                if (user != null) {
                    val name = user.optString("name", "")
                    val email = user.optString("email", "")

                    if (name.isNotEmpty() && email.isNotEmpty()) {
                        sessionManager.saveUser(name, email)
                        preferences.edit()
                            .putString("username", name)
                            .putString("user_email", email)
                            .apply()

                        updateProfileViews(name, email)
                    }
                }
            } catch (e: Exception) {
                // Silently fall back to cached session user info if offline
            }
        }
    }

    private fun updateProfileViews(name: String, email: String) {
        tvProfileName.text = name
        tvEmail.text = email

        val parts = name.trim().split("\\s+".toRegex())
        val initials = when {
            parts.size >= 2 -> "${
                parts[0].firstOrNull()?.uppercaseChar() ?: ""
            }${parts[1].firstOrNull()?.uppercaseChar() ?: ""}"

            parts.isNotEmpty() && parts[0].isNotEmpty() -> "${parts[0].first().uppercaseChar()}"
            else -> "N"
        }
        tvProfileInitials.text = initials
    }

    private fun showEditProfileDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val etEditName = view.findViewById<TextInputEditText>(R.id.etEditName)
        etEditName.setText(tvProfileName.text.toString())

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val newName = etEditName.text.toString().trim()
                if (newName.isEmpty()) {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                updateUserName(newName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUserName(newName: String) {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getToken()
                if (token.isNullOrEmpty()) {
                    Toast.makeText(this@SettingsActivity, "Please login again", Toast.LENGTH_SHORT)
                        .show()
                    return@launch
                }

                val result = withContext(Dispatchers.IO) {
                    val url = URL("$BASE_URL/auth/me")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "PUT"
                    connection.setRequestProperty("Authorization", "Bearer $token")
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.doOutput = true

                    val json = JSONObject()
                    json.put("name", newName)

                    connection.outputStream.use { output ->
                        output.write(json.toString().toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    if (responseCode !in 200..299) {
                        throw Exception("HTTP $responseCode")
                    }

                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    connection.disconnect()
                    JSONObject(response)
                }

                val user = result.optJSONObject("user")
                val updatedName = user?.optString("name", newName) ?: newName
                val updatedEmail =
                    user?.optString("email", tvEmail.text.toString()) ?: tvEmail.text.toString()

                sessionManager.saveUser(updatedName, updatedEmail)
                preferences.edit()
                    .putString("username", updatedName)
                    .putString("user_email", updatedEmail)
                    .apply()

                updateProfileViews(updatedName, updatedEmail)
                Toast.makeText(
                    this@SettingsActivity,
                    "Profile updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    this@SettingsActivity,
                    "Profile update failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showChangePasswordDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val etOldPassword = view.findViewById<TextInputEditText>(R.id.etOldPassword)
        val etNewPassword = view.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmNewPassword = view.findViewById<TextInputEditText>(R.id.etConfirmNewPassword)

        AlertDialog.Builder(this)
            .setView(view)
            .setPositiveButton("Change") { _, _ ->
                val oldPass = etOldPassword.text.toString()
                val newPass = etNewPassword.text.toString()
                val confirmPass = etConfirmNewPassword.text.toString()

                if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                    Toast.makeText(this, "All password fields are required", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                if (newPass.length < 6) {
                    Toast.makeText(
                        this,
                        "New password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (newPass != confirmPass) {
                    Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                performChangePassword(oldPass, newPass)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performChangePassword(oldPass: String, newPass: String) {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getToken()
                if (token.isNullOrEmpty()) return@launch

                val result = withContext(Dispatchers.IO) {
                    val url = URL("$BASE_URL/auth/change-password")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Authorization", "Bearer $token")
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.doOutput = true

                    val json = JSONObject()
                    json.put("old_password", oldPass)
                    json.put("new_password", newPass)

                    connection.outputStream.use { output ->
                        output.write(json.toString().toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    val responseText = if (responseCode in 200..299) {
                        connection.inputStream.bufferedReader().use { it.readText() }
                    } else {
                        connection.errorStream?.bufferedReader()?.use { it.readText() }
                            ?: "Password change failed"
                    }

                    connection.disconnect()

                    if (responseCode !in 200..299) {
                        var errorMsg = "Password change failed"
                        try {
                            val errJson = JSONObject(responseText)
                            errorMsg = errJson.optString("detail", errorMsg)
                        } catch (e: Exception) {
                        }
                        throw Exception(errorMsg)
                    }

                    JSONObject(responseText)
                }

                val msg = result.optString("message", "Password changed successfully")
                Toast.makeText(this@SettingsActivity, msg, Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this@SettingsActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun performDeleteAccount() {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getToken()
                if (!token.isNullOrEmpty()) {
                    withContext(Dispatchers.IO) {
                        val url = URL("$BASE_URL/auth/me")
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "DELETE"
                        connection.setRequestProperty("Authorization", "Bearer $token")
                        connection.connectTimeout = 10000
                        connection.readTimeout = 10000
                        connection.responseCode
                        connection.disconnect()
                    }
                }
            } catch (e: Exception) {
                // Proceed with local logout regardless
            }

            preferences.edit().clear().apply()
            sessionManager.logout()

            Toast.makeText(this@SettingsActivity, "Account deleted", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@SettingsActivity, MainLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun performClearAllChatHistory() {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getToken()
                if (!token.isNullOrEmpty()) {
                    withContext(Dispatchers.IO) {
                        NexusClient.api.deleteAllChats("Bearer $token").execute()
                    }
                }
            } catch (e: Exception) {
                // Silently clear local history if offline
            }
            preferences.edit().remove("chat_history").apply()
            sendBroadcast(Intent("com.jayshil.a24012021038_chatbot.CLEAR_ALL_CHATS"))
            Toast.makeText(this@SettingsActivity, "Chat history cleared", Toast.LENGTH_SHORT).show()
        }
    }
}