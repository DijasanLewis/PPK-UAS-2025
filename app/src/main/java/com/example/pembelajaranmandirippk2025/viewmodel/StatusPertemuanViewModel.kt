package com.example.pembelajaranmandirippk2025.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.adapter.StatusPertemuanAdapter
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.util.SessionManager
import com.example.pembelajaranmandirippk2025.repository.Repository
import kotlinx.coroutines.launch

class StatusPertemuanViewModel(private val sessionManager: SessionManager) : ViewModel() {
    private val repository = Repository()

    private val _statusList = MutableLiveData<ApiResult<List<StatusPertemuanDTO>>>()
    val statusList: LiveData<ApiResult<List<StatusPertemuanDTO>>> = _statusList

    private val _pertemuanList = MutableLiveData<ApiResult<List<PertemuanDTO>>>()
    val pertemuanList: LiveData<ApiResult<List<PertemuanDTO>>> = _pertemuanList

    private val _updateResult = MutableLiveData<ApiResult<StatusPertemuanDTO>>()
    val updateResult: LiveData<ApiResult<StatusPertemuanDTO>> = _updateResult

    private var pertemuanMap: Map<Long, PertemuanDTO> = emptyMap()

    fun loadPertemuan() {
        viewModelScope.launch {
            _pertemuanList.value = ApiResult.Loading
            _pertemuanList.value = repository.getAllPertemuan()
        }
    }

    fun setPertemuanMap(map: Map<Long, PertemuanDTO>) {
        pertemuanMap = map
    }

    fun getPertemuanName(pertemuanId: Long): String? {
        return pertemuanMap[pertemuanId]?.namaPertemuan
    }

    fun loadStatusByMahasiswa(mahasiswaId: Long) {
        viewModelScope.launch {
            _statusList.value = ApiResult.Loading
            try {
                val result = repository.getStatusByMahasiswa(mahasiswaId)  // Panggil API untuk status mahasiswa
                _statusList.value = result  // Update status list dengan data yang baru
            } catch (e: Exception) {
                _statusList.value = ApiResult.Error(e)
            }
        }
    }

    fun updateStatus(status: StatusPertemuanDTO, currentPertemuanId: Long = -1L) {
        viewModelScope.launch {
            _updateResult.value = ApiResult.Loading
            try {
                val userId = sessionManager.getUserId()
                val result = repository.updateStatus(status, userId)
                _updateResult.value = result
                
                // Refresh data jika currentPertemuanId valid
                if (currentPertemuanId != -1L) {
                    loadStatusByPertemuan(currentPertemuanId)
                }
            } catch (e: Exception) {
                _updateResult.value = ApiResult.Error(e)
            }
        }
    }

    fun loadStatusByPertemuan(pertemuanId: Long) {
        viewModelScope.launch {
            Log.d("StatusPertemuanViewModel", "Loading status for pertemuan ID: $pertemuanId")
            _statusList.value = ApiResult.Loading

            try {
                val result = repository.getStatusByPertemuan(pertemuanId)
                Log.d("StatusPertemuanViewModel", "Status load result: $result")
                _statusList.value = result
            } catch (e: Exception) {
                Log.e("StatusPertemuanViewModel", "Error loading status", e)
                _statusList.value = ApiResult.Error(e)
            }
        }
    }

    fun getPertemuanList(): List<PertemuanDTO> {
        return when (val result = pertemuanList.value) {
            is ApiResult.Success -> result.data
            else -> emptyList()
        }
    }
}