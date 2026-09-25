package com.jayshil.a24012021038_chatbot

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.jayshil.a24012021038_chatbot.api.ChatDto
import com.jayshil.a24012021038_chatbot.api.ChatMessageDto
import com.jayshil.a24012021038_chatbot.api.CreateChatRequest
import com.jayshil.a24012021038_chatbot.api.NexusClient
import com.jayshil.a24012021038_chatbot.api.UpdateChatRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var historyAdapter: ChatHistoryAdapter
    private lateinit var recyclerChat: RecyclerView
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var etMessage: EditText

    private val messages = mutableListOf<ChatMessage>()
    private val chatHistories = mutableListOf<ChatHistory>()
    private var currentChat: ChatHistory? = null

    private var speechRecognizer: SpeechRecognizer? = null

    // Attachment Launchers
    private val pickGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(it) ?: "image.jpg"
            appendAttachmentText("[Attached Image: $fileName]")
        }
    }

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(it) ?: "document.pdf"
            appendAttachmentText("[Attached File: $fileName]")
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            appendAttachmentText("[Attached Captured Photo]")
        }
    }

    // Audio Permission Launcher
    private val requestRecordAudioPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startSpeechToText()
        } else {
            Toast.makeText(this, "Microphone permission is required for speech recognition.", Toast.LENGTH_LONG).show()
        }
    }

    private val clearChatReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.jayshil.a24012021038_chatbot.CLEAR_CURRENT_CHAT" -> {
                    val chatToDelete = currentChat
                    if (chatToDelete != null && chatToDelete.id > 0) {
                        deleteChatFromBackend(chatToDelete.id)
                        chatHistories.removeAll { it.id == chatToDelete.id }
                        historyAdapter.notifyDataSetChanged()
                    }
                    messages.clear()
                    if (::chatAdapter.isInitialized) {
                        chatAdapter.notifyDataSetChanged()
                    }
                    currentChat = null
                }
                "com.jayshil.a24012021038_chatbot.CLEAR_ALL_CHATS" -> {
                    chatHistories.clear()
                    historyAdapter.notifyDataSetChanged()
                    messages.clear()
                    if (::chatAdapter.isInitialized) {
                        chatAdapter.notifyDataSetChanged()
                    }
                    currentChat = null
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerChat = findViewById(R.id.recyclerChat)
        recyclerHistory = findViewById(R.id.recyclerHistory)
        etMessage = findViewById(R.id.etMessage)

        val sendCard = findViewById<MaterialCardView>(R.id.sendCard)
        drawerLayout = findViewById(R.id.drawerLayout)
        val menuCard = findViewById<MaterialCardView>(R.id.menuCard)
        val settingsCard = findViewById<MaterialCardView>(R.id.settingsCard)
        val historyDrawer = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.historyDrawer)
        val newChatButton = findViewById<MaterialButton>(R.id.btnNewChat)
        val addCard = findViewById<MaterialCardView>(R.id.addCard)
        val micCard = findViewById<MaterialCardView>(R.id.micCard)

        chatAdapter = ChatAdapter(messages)
        recyclerChat.layoutManager = LinearLayoutManager(this)
        recyclerChat.adapter = chatAdapter

        historyAdapter = ChatHistoryAdapter(
            histories = chatHistories,
            onChatClick = { selectedChat ->
                currentChat = selectedChat
                messages.clear()
                messages.addAll(selectedChat.messages)
                chatAdapter.notifyDataSetChanged()

                if (chatAdapter.itemCount > 0) {
                    recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)
                }
                drawerLayout.closeDrawer(historyDrawer)
            },
            onChatDelete = { chatToDelete ->
                if (chatToDelete.id > 0) {
                    deleteChatFromBackend(chatToDelete.id)
                }
                if (currentChat?.id == chatToDelete.id) {
                    currentChat = null
                    messages.clear()
                    chatAdapter.notifyDataSetChanged()
                }
            },
            onChatRename = { chatToRename, newTitle ->
                if (chatToRename.id > 0) {
                    syncChatToBackend(chatToRename)
                }
            }
        )

        recyclerHistory.layoutManager = LinearLayoutManager(this)
        recyclerHistory.adapter = historyAdapter

        val intentFilter = IntentFilter().apply {
            addAction("com.jayshil.a24012021038_chatbot.CLEAR_CURRENT_CHAT")
            addAction("com.jayshil.a24012021038_chatbot.CLEAR_ALL_CHATS")
        }
        registerReceiver(
            clearChatReceiver,
            intentFilter,
            Context.RECEIVER_NOT_EXPORTED
        )

        // Send message listener
        sendCard.setOnClickListener { sendMessage() }

        // Send with Enter setting support
        etMessage.setOnEditorActionListener { _, actionId, event ->
            val sendEnterEnabled = getSharedPreferences("nexus_settings", MODE_PRIVATE).getBoolean("send_enter", true)
            if (sendEnterEnabled && (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN))) {
                sendMessage()
                true
            } else {
                false
            }
        }

        // Plus / Attachment button listener
        addCard.setOnClickListener { showAttachmentOptionsDialog() }

        // Microphone button listener
        micCard.setOnClickListener { checkAudioPermissionAndStartSpeech() }

        newChatButton.setOnClickListener {
            currentChat = null
            messages.clear()
            chatAdapter.notifyDataSetChanged()
            drawerLayout.closeDrawer(historyDrawer)
            recyclerChat.scrollToPosition(0)
        }

        menuCard.setOnClickListener { drawerLayout.openDrawer(historyDrawer) }

        settingsCard.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        val mainView = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        loadChatHistoryFromBackend()
    }

    override fun onResume() {
        super.onResume()
        if (::chatAdapter.isInitialized) {
            val showTimestamps = getSharedPreferences("nexus_settings", MODE_PRIVATE).getBoolean("timestamps", false)
            chatAdapter.setShowTimestamps(showTimestamps)
        }
    }

    private fun loadChatHistoryFromBackend() {
        val token = sessionManager.getToken() ?: return
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NexusClient.api.getChats("Bearer $token").execute()
                }
                if (response.isSuccessful) {
                    val body = response.body()
                    val chatsDto = body?.chats
                    if (chatsDto != null) {
                        chatHistories.clear()
                        for (dto in chatsDto) {
                            val msgList = dto.messages.map { m ->
                                ChatMessage(
                                    message = m.message,
                                    isUser = m.isUser,
                                    isTyping = false,
                                    timestamp = m.timestamp
                                )
                            }.toMutableList()

                            val history = ChatHistory(
                                id = dto.id,
                                title = dto.title,
                                messages = msgList
                            )
                            chatHistories.add(history)
                        }
                        historyAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                // Silently maintain local history list if offline
            }
        }
    }

    private fun sendMessage() {
        val message = etMessage.text.toString().trim()
        if (message.isEmpty()) return

        val userMessage = ChatMessage(
            message = message,
            isUser = true,
            isTyping = false,
            timestamp = getCurrentTimestamp()
        )

        chatAdapter.addMessage(userMessage)
        recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)
        etMessage.text.clear()

        val typingMessage = ChatMessage(
            message = "",
            isUser = false,
            isTyping = true
        )

        chatAdapter.addMessage(typingMessage)
        recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)

        sendToAI(message, userMessage)
    }

    private fun sendToAI(userMessageText: String, userMessage: ChatMessage) {
        lifecycleScope.launch {
            try {
                val token = sessionManager.getToken()
                val reply = withContext(Dispatchers.IO) {
                    val url = URL(NexusClient.getBaseUrl() + "chat")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.setRequestProperty("Accept", "application/json")
                    if (!token.isNullOrEmpty()) {
                        connection.setRequestProperty("Authorization", "Bearer $token")
                    }
                    connection.connectTimeout = 10000
                    connection.readTimeout = 60000
                    connection.doOutput = true

                    val json = JSONObject()
                    json.put("message", userMessageText)

                    connection.outputStream.use { output ->
                        output.write(json.toString().toByteArray(Charsets.UTF_8))
                    }

                    val responseCode = connection.responseCode
                    if (responseCode != 200) {
                        throw Exception("Server Error: $responseCode")
                    }

                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    connection.disconnect()

                    val responseJson = JSONObject(response)
                    responseJson.optString("reply", "No response from AI")
                }

                chatAdapter.removeTypingMessage()

                val aiMessage = ChatMessage(
                    message = reply,
                    isUser = false,
                    isTyping = false,
                    timestamp = getCurrentTimestamp()
                )

                chatAdapter.addMessage(aiMessage)
                recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)

                if (currentChat == null) {
                    val newTitle = createChatTitle(userMessageText)
                    val newChatHistory = ChatHistory(
                        id = 0,
                        title = newTitle,
                        messages = mutableListOf(userMessage, aiMessage)
                    )
                    currentChat = newChatHistory
                    chatHistories.add(0, newChatHistory)
                    historyAdapter.notifyDataSetChanged()
                    createChatOnBackend(newChatHistory)
                } else {
                    currentChat!!.messages.add(userMessage)
                    currentChat!!.messages.add(aiMessage)
                    historyAdapter.notifyDataSetChanged()
                    syncChatToBackend(currentChat!!)
                }

            } catch (e: Exception) {
                chatAdapter.removeTypingMessage()
                Toast.makeText(
                    this@MainActivity,
                    "AI Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun createChatOnBackend(chat: ChatHistory) {
        val token = sessionManager.getToken() ?: return
        lifecycleScope.launch {
            try {
                val dtoList = chat.messages.filter { !it.isTyping }.map {
                    ChatMessageDto(it.message, it.isUser, it.timestamp)
                }
                val request = CreateChatRequest(chat.title, dtoList)
                val response = withContext(Dispatchers.IO) {
                    NexusClient.api.createChat("Bearer $token", request).execute()
                }
                if (response.isSuccessful) {
                    val createdChat = response.body()?.chat
                    if (createdChat != null) {
                        chat.id = createdChat.id
                        historyAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                // Silently keep local state if offline
            }
        }
    }

    private fun syncChatToBackend(chat: ChatHistory) {
        val token = sessionManager.getToken() ?: return
        if (chat.id <= 0) return
        lifecycleScope.launch {
            try {
                val dtoList = chat.messages.filter { !it.isTyping }.map {
                    ChatMessageDto(it.message, it.isUser, it.timestamp)
                }
                val request = UpdateChatRequest(chat.title, dtoList)
                withContext(Dispatchers.IO) {
                    NexusClient.api.updateChat("Bearer $token", chat.id, request).execute()
                }
            } catch (e: Exception) {
                // Silently keep local state if offline
            }
        }
    }

    private fun deleteChatFromBackend(chatId: Long) {
        val token = sessionManager.getToken() ?: return
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    NexusClient.api.deleteChat("Bearer $token", chatId).execute()
                }
            } catch (e: Exception) {
                // Silently handle offline delete
            }
        }
    }

    // =========================================================
    // SPEECH RECOGNITION (MICROPHONE)
    // =========================================================

    private fun checkAudioPermissionAndStartSpeech() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech Recognition is unavailable on this device.", Toast.LENGTH_LONG).show()
            return
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startSpeechToText()
        } else {
            requestRecordAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startSpeechToText() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            }

            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Toast.makeText(this@MainActivity, "Listening...", Toast.LENGTH_SHORT).show()
                }

                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}

                override fun onError(error: Int) {
                    val message = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Speech recognition failed"
                    }
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val spokenText = matches[0]
                        val currentText = etMessage.text.toString()
                        if (currentText.isNotEmpty()) {
                            etMessage.setText("$currentText $spokenText")
                        } else {
                            etMessage.setText(spokenText)
                        }
                        etMessage.setSelection(etMessage.text.length)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            speechRecognizer?.startListening(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start microphone: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // =========================================================
    // ATTACHMENT OPTIONS (+ BUTTON)
    // =========================================================

    private fun showAttachmentOptionsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_attachment_options, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<MaterialCardView>(R.id.btnOptionCamera).setOnClickListener {
            dialog.dismiss()
            takePictureLauncher.launch(null)
        }

        dialogView.findViewById<MaterialCardView>(R.id.btnOptionGallery).setOnClickListener {
            dialog.dismiss()
            pickGalleryLauncher.launch("image/*")
        }

        dialogView.findViewById<MaterialCardView>(R.id.btnOptionFile).setOnClickListener {
            dialog.dismiss()
            pickFileLauncher.launch("*/*")
        }

        dialog.show()
    }

    private fun appendAttachmentText(text: String) {
        val current = etMessage.text.toString().trim()
        if (current.isEmpty()) {
            etMessage.setText(text)
        } else {
            etMessage.setText("$current\n$text")
        }
        etMessage.setSelection(etMessage.text.length)
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name ?: uri.lastPathSegment
    }

    private fun createChatTitle(message: String): String {
        val title = message.trim()
        return if (title.length > 30) {
            title.take(30) + "..."
        } else {
            title
        }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        try {
            unregisterReceiver(clearChatReceiver)
        } catch (e: Exception) {}
        super.onDestroy()
    }
}