package com.example.pembelajaranmandirippk2025.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pembelajaranmandirippk2025.util.SessionManager

class StatusPertemuanViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusPertemuanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatusPertemuanViewModel(sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
