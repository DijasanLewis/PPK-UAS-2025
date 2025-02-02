package com.example.pembelajaranmandirippk2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = Repository()
    private val _pertemuanList = MutableLiveData<ApiResult<List<PertemuanDTO>>>()
    val pertemuanList: LiveData<ApiResult<List<PertemuanDTO>>> = _pertemuanList

    private val _authError = MutableLiveData<Boolean>()
    val authError: LiveData<Boolean> = _authError

    fun loadPertemuan() {
        viewModelScope.launch {
            _pertemuanList.value = ApiResult.Loading
            when (val result = repository.getAllPertemuan()) {
                is ApiResult.Error -> {
                    if (result.exception.message?.contains("Unauthorized") == true) {
                        _authError.value = true
                    }
                    _pertemuanList.value = result
                }
                else -> _pertemuanList.value = result
            }
        }
    }
}