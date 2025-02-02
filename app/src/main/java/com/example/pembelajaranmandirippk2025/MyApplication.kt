package com.example.pembelajaranmandirippk2025

import android.app.Application
import com.example.pembelajaranmandirippk2025.api.RetrofitClient
import com.example.pembelajaranmandirippk2025.util.SessionManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize token from SessionManager
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken()
        if (!token.isNullOrEmpty()) {
            RetrofitClient.setToken(token)
        }
    }
}
