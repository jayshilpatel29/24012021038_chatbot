package com.jayshil.a24012021038_chatbot

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerChat: RecyclerView

    private val messages = mutableListOf<ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)



        recyclerChat = findViewById(R.id.recyclerChat)

        val etMessage = findViewById<android.widget.EditText>(
            R.id.etMessage
        )

        val sendCard = findViewById<MaterialCardView>(
            R.id.sendCard
        )

        chatAdapter = ChatAdapter(messages)

        recyclerChat.adapter = chatAdapter
        recyclerChat.layoutManager = LinearLayoutManager(this)



        sendCard.setOnClickListener {

            val message = etMessage.text.toString().trim()

            if (message.isEmpty()) {
                return@setOnClickListener
            }

            // Show user's message
            chatAdapter.addMessage(
                ChatMessage(
                    message = message,
                    isUser = true
                )
            )

            etMessage.text.clear()

            recyclerChat.scrollToPosition(
                chatAdapter.itemCount - 1
            )

            // Send message to Gemini through backend
            sendToAI(message)
        }



        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
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



        val drawerLayout = findViewById<DrawerLayout>(
            R.id.drawerLayout
        )

        val menuCard = findViewById<MaterialCardView>(
            R.id.menuCard
        )

        val historyDrawer =
            findViewById<androidx.constraintlayout.widget.ConstraintLayout>(
                R.id.historyDrawer
            )

        menuCard.setOnClickListener {
            drawerLayout.openDrawer(historyDrawer)
        }



        val settingsCard = findViewById<MaterialCardView>(
            R.id.settingsCard
        )

        settingsCard.setOnClickListener {

            val intent = Intent(
                this,
                SettingsActivity::class.java
            )

            startActivity(intent)
        }
    }


    private fun sendToAI(message: String) {

        lifecycleScope.launch {

            try {

                val reply = withContext(Dispatchers.IO) {

                    val url = URL(
                        "http://10.186.103.157:8000/chat"
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

                    connection.doOutput = true

                    val json = JSONObject()
                    json.put("message", message)

                    connection.outputStream.use { output ->
                        output.write(
                            json.toString().toByteArray()
                        )
                    }

                    val responseCode = connection.responseCode

                    if (responseCode != 200) {
                        throw Exception(
                            "Server Error: $responseCode"
                        )
                    }

                    val response =
                        connection.inputStream
                            .bufferedReader()
                            .use { it.readText() }

                    connection.disconnect()

                    val responseJson = JSONObject(response)

                    responseJson.getString("reply")
                }

                // Add Gemini reply to RecyclerView
                chatAdapter.addMessage(
                    ChatMessage(
                        message = reply,
                        isUser = false
                    )
                )

                recyclerChat.scrollToPosition(
                    chatAdapter.itemCount - 1
                )

            } catch (e: Exception) {

                Toast.makeText(
                    this@MainActivity,
                    "AI Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }}