package com.example.pembelajaranmandirippk2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.api.RetrofitClient
import com.example.pembelajaranmandirippk2025.repository.Repository
import kotlinx.coroutines.launch

class PertemuanViewModel : ViewModel() {
    private val repository = Repository()

    private val _pertemuanList = MutableLiveData<ApiResult<List<PertemuanDTO>>>()
    val pertemuanList: LiveData<ApiResult<List<PertemuanDTO>>> = _pertemuanList

    private val _pertemuanDetail = MutableLiveData<ApiResult<PertemuanDTO>>()
    val pertemuanDetail: LiveData<ApiResult<PertemuanDTO>> = _pertemuanDetail

    private val _operationResult = MutableLiveData<ApiResult<Unit>>()
    val operationResult: LiveData<ApiResult<Unit>> = _operationResult

    fun loadPertemuan() {
        viewModelScope.launch {
            _pertemuanList.value = ApiResult.Loading
            try {
                val result = repository.getAllPertemuan()
                _pertemuanList.value = result
            } catch (e: Exception) {
                _pertemuanList.value = ApiResult.Error(e)
            }
        }
    }

    fun getPertemuanById(id: Long) {
        viewModelScope.launch {
            _pertemuanDetail.value = ApiResult.Loading
            try {
                val result = repository.getPertemuan(id)
                _pertemuanDetail.value = result
            } catch (e: Exception) {
                _pertemuanDetail.value = ApiResult.Error(e)
            }
        }
    }

    fun createPertemuan(pertemuan: PertemuanDTO) {
        viewModelScope.launch {
            _operationResult.value = ApiResult.Loading
            try {
                val response = repository.createPertemuan(pertemuan)
                when (response) {
                    is ApiResult.Success -> {
                        _operationResult.value = ApiResult.Success(Unit)
                        loadPertemuan() // Refresh list after creation
                    }
                    is ApiResult.Error -> _operationResult.value = ApiResult.Error(response.exception)
                    ApiResult.Loading -> _operationResult.value = ApiResult.Loading
                }
            } catch (e: Exception) {
                _operationResult.value = ApiResult.Error(e)
            }
        }
    }

    fun updatePertemuan(pertemuan: PertemuanDTO) {
        viewModelScope.launch {
            _operationResult.value = ApiResult.Loading
            try {
                val response = repository.updatePertemuan(pertemuan.id!!, pertemuan)
                when (response) {
                    is ApiResult.Success -> {
                        _operationResult.value = ApiResult.Success(Unit)
                        loadPertemuan() // Refresh list after update
                    }
                    is ApiResult.Error -> _operationResult.value = ApiResult.Error(response.exception)
                    ApiResult.Loading -> _operationResult.value = ApiResult.Loading
                }
            } catch (e: Exception) {
                _operationResult.value = ApiResult.Error(e)
            }
        }
    }

    fun deletePertemuan(pertemuanId: Long) {
        viewModelScope.launch {
            _operationResult.value = ApiResult.Loading
            try {
                val token = RetrofitClient.getToken()
                if (token.isNullOrEmpty()) {
                    _operationResult.value = ApiResult.Error(Exception("Token tidak valid, silakan login ulang"))
                    return@launch
                }

                val result = repository.deletePertemuan(pertemuanId)
                when (result) {
                    is ApiResult.Success -> {
                        _operationResult.value = ApiResult.Success(Unit)
                        loadPertemuan() // Refresh list after successful deletion
                    }
                    is ApiResult.Error -> {
                        _operationResult.value = ApiResult.Error(result.exception)
                    }
                    ApiResult.Loading -> _operationResult.value = ApiResult.Loading
                }
            } catch (e: Exception) {
                _operationResult.value = ApiResult.Error(e)
            }
        }
    }
}