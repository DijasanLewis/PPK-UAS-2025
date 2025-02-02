package com.example.pembelajaranmandirippk2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pembelajaranmandirippk2025.UserDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.repository.Repository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = Repository()
    private val _registerResult = MutableLiveData<ApiResult<UserDTO>>()
    val registerResult: LiveData<ApiResult<UserDTO>> = _registerResult

    fun register(user: UserDTO) {
        viewModelScope.launch {
            _registerResult.value = ApiResult.Loading
            _registerResult.value = repository.register(user)
        }
    }
}