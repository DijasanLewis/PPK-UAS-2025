package com.example.pembelajaranmandirippk2025.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pembelajaranmandirippk2025.UserDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.repository.Repository
import com.example.pembelajaranmandirippk2025.util.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(context: Context) : ViewModel() {
    private val repository = Repository()
    private val sessionManager: SessionManager = SessionManager(context)

    private val _userProfile = MutableLiveData<ApiResult<UserDTO>>()
    val userProfile: LiveData<ApiResult<UserDTO>> = _userProfile

    private val _updateProfileResult = MutableLiveData<ApiResult<UserDTO>>()
    val updateProfileResult: LiveData<ApiResult<UserDTO>> = _updateProfileResult

    private val _changePasswordResult = MutableLiveData<ApiResult<Unit>>()
    val changePasswordResult: LiveData<ApiResult<Unit>> = _changePasswordResult

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = ApiResult.Loading
            _userProfile.value = repository.getUserProfile()
        }
    }

    fun updateUserProfile(userDTO: UserDTO) {
        viewModelScope.launch {
            _updateProfileResult.value = ApiResult.Loading
            val userId = sessionManager.getUserId()
            if (userId == -1L) {
                _updateProfileResult.value = ApiResult.Error(Exception("User not logged in"))
                return@launch
            }

            try {
                val result = repository.updateUser(userId, userDTO)
                Log.d("ProfileViewModel", "Update result: $result")

                when (result) {
                    is ApiResult.Success -> {
                        _updateProfileResult.value = result
                    }
                    is ApiResult.Error -> {
                        Log.e("ProfileViewModel", "Update error: ${result.exception.message}")
                        _updateProfileResult.value = result
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Update exception: ${e.message}")
                _updateProfileResult.value = ApiResult.Error(e)
            }
        }
    }

    fun changePassword(userId: Long, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordResult.value = ApiResult.Loading
            val result = repository.changePassword(userId, oldPassword, newPassword)
            _changePasswordResult.value = result
        }
    }
}