package com.jayshil.a24012021038_chatbot

class UserRepository {

    private val users = mutableListOf<User>()

    fun register(email: String, password: String): Boolean {
        if (users.any { it.email == email }) {
            return false
        }

        users.add(
            User(
                id = users.size + 1,
                email = email,
                password = password
            )
        )

        return true
    }

    fun login(email: String, password: String): Boolean {
        return users.any {
            it.email == email && it.password == password
        }
    }
}