package com.jayshil.a24012021038_chatbot

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<TextView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}