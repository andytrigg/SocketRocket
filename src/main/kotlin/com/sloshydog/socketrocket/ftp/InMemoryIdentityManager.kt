package com.sloshydog.socketrocket.ftp

import at.favre.lib.crypto.bcrypt.BCrypt

object InMemoryIdentityManager : IdentityManager {
    private val users = mutableMapOf<String, String>() // Stores username → hashed password

    init {
        // Preload some example users
        addUser("testuser", "password123")
        addUser("admin", "securepass")
    }

    fun addUser(username: String, password: String) {
        val hashedPassword = hashPassword(password)
        users[username] = hashedPassword
    }

    override fun isValidUser(username: String): Boolean {
        return users.containsKey(username)
    }

    override fun isValidPassword(username: String, password: String): Boolean {
        val storedHash = users[username] ?: return false
        return verifyPassword(password, storedHash)
    }

    private fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray()) // Hash password securely
    }

    private fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
    }
}