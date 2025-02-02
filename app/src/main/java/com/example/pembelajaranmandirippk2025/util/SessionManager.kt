package com.example.pembelajaranmandirippk2025.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.pembelajaranmandirippk2025.api.RetrofitClient

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveAuthToken(token: String) {
        Log.d("SessionManager", "Saving token: ${token.take(20)}...")
        editor.putString(KEY_TOKEN, token)
        editor.apply()

        // Immediately verify token was saved
        val savedToken = getAuthToken()
        Log.d("SessionManager", "Verified saved token: ${savedToken?.take(20)}...")

        // Update RetrofitClient token
        RetrofitClient.setToken(token)
    }

    fun getAuthToken(): String? {
        val token = sharedPreferences.getString(KEY_TOKEN, null)
        Log.d("SessionManager", "Getting token: ${token?.take(20)}...")
        return token
    }

    fun saveUserId(userId: Long) {
        editor.putLong(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1L)
    }

    fun saveUserRole(role: String) {
        editor.putString(KEY_USER_ROLE, role)
        editor.apply()
    }

    fun getUserRole(): String? {
        return sharedPreferences.getString(KEY_USER_ROLE, null)
    }

    fun saveUserEmail(email: String) {
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREF_NAME = "PembelajaranMandiriPPKPrefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_EMAIL = "user_email"
    }
}