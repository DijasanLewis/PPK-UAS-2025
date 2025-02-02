package com.example.pembelajaranmandirippk2025.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pembelajaranmandirippk2025.AuthRequest
import com.example.pembelajaranmandirippk2025.UserDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.api.RetrofitClient
import com.example.pembelajaranmandirippk2025.repository.Repository
import com.example.pembelajaranmandirippk2025.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException

class LoginViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val repository = Repository() // Ubah ini untuk menggunakan Repository class
    private val _loginResult = MutableLiveData<ApiResult<String>>()
    val loginResult: LiveData<ApiResult<String>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = ApiResult.Loading
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.login(email, password)
                }

                when (response) {
                    is ApiResult.Success -> {
                        // Save token
                        val token = response.data
                        RetrofitClient.setToken(token)
                        sessionManager.saveAuthToken(token)

                        // Debug: Log token after login
                        Log.d("LoginViewModel", "Token saved: ${token.take(20)}...")

                        // Get user profile
                        when (val profileResponse = repository.getUserProfile()) {
                            is ApiResult.Success -> {
                                val userData = profileResponse.data
                                sessionManager.saveUserId(userData.id ?: -1L)
                                sessionManager.saveUserRole(userData.role)
                                Log.d("LoginViewModel", "User role saved: ${userData.role}")
                            }
                            is ApiResult.Error -> {
                                Log.e("LoginViewModel", "Failed to get user profile", profileResponse.exception)
                            }
                            is ApiResult.Loading -> {}
                        }

                        _loginResult.value = ApiResult.Success(token)
                    }
                    is ApiResult.Error -> {
                        Log.e("LoginViewModel", "Login failed", response.exception)
                        _loginResult.value = ApiResult.Error(response.exception)
                    }
                    is ApiResult.Loading -> {
                        _loginResult.value = ApiResult.Loading
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login exception", e)
                _loginResult.value = ApiResult.Error(e)
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return !sessionManager.getAuthToken().isNullOrEmpty()
    }
}