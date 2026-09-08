package com.jayshil.a24012021038_chatbot

import android.content.Context

class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "NexusSession",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        preferences.edit()
            .putString("access_token", token)
            .apply()
    }

    fun getToken(): String? {
        return preferences.getString("access_token", null)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun clearSession() {
        preferences.edit()
            .clear()
            .apply()
    }

    fun logout() {
        clearSession()
    }
}