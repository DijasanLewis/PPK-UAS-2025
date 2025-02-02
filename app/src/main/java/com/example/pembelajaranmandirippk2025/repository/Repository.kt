package com.example.pembelajaranmandirippk2025.repository

import android.util.Log
import com.example.pembelajaranmandirippk2025.AuthRequest
import com.example.pembelajaranmandirippk2025.ChangePasswordRequest
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.UserDTO
import com.example.pembelajaranmandirippk2025.api.ApiResult
import com.example.pembelajaranmandirippk2025.api.RetrofitClient
import com.example.pembelajaranmandirippk2025.util.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class Repository {
    private val apiService = RetrofitClient.apiService
    private lateinit var sessionManager: SessionManager

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                when (response.code()) {
                    200, 201, 204 -> {
                        response.body()?.let {
                            ApiResult.Success(it)
                        } ?: ApiResult.Error(Exception("Empty response body"))
                    }
                    401, 403 -> {
                        // Token might be expired
                        ApiResult.Error(Exception("Unauthorized. Please login again."))
                    }
                    else -> ApiResult.Error(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }
    }

    suspend fun login(email: String, password: String): ApiResult<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(AuthRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    RetrofitClient.setToken(it.token)
                    ApiResult.Success(it.token)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Login failed"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun register(user: UserDTO): ApiResult<UserDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(user)
            if (response.isSuccessful) {
                response.body()?.let { ApiResult.Success(it) }
                    ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun updateUser(id: Long, user: UserDTO): ApiResult<UserDTO> = withContext(Dispatchers.IO) {
        try {
            val currentUser = apiService.getCurrentUserProfile().body()
            val updatedUser = user.copy(password = currentUser?.password ?: "")
            val response = apiService.updateUser(id, updatedUser)

            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Data kosong"))
            } else {
                ApiResult.Error(Exception("Gagal memperbarui: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun changePassword(
        userId: Long,
        oldPassword: String,
        newPassword: String
    ): ApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.changePassword(
                userId,
                ChangePasswordRequest(oldPassword, newPassword)
            )
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(Exception("Password change failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getPertemuan(id: Long): ApiResult<PertemuanDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPertemuan(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to get pertemuan: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getAllPertemuan(): ApiResult<List<PertemuanDTO>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAllPertemuan()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to get pertemuan list: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getStatusByMahasiswa(mahasiswaId: Long): ApiResult<List<StatusPertemuanDTO>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStatusByMahasiswa(mahasiswaId)
                if (response.isSuccessful) {
                    val body = response.body()
                    response.body()?.let { ApiResult.Success(it) }
                        ?: run {
                            ApiResult.Error(Exception("Empty response body"))
                        }
                } else {
                    ApiResult.Error(Exception("Failed to fetch status"))
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }

    suspend fun updateStatus(status: StatusPertemuanDTO, userId: Long): ApiResult<StatusPertemuanDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateStatus(status, userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to update status: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getUserById(userId: Long): ApiResult<UserDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUser(userId)
            if (response.isSuccessful) {
                response.body()?.let {
                    sessionManager.saveUserRole(it.role)
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to fetch user profile"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getUserProfile(): ApiResult<UserDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUserProfile()
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: run {
                    ApiResult.Error(Exception("Empty response body"))
                }
            } else {
                ApiResult.Error(Exception("Failed to fetch profile: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun createPertemuan(pertemuan: PertemuanDTO): ApiResult<PertemuanDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createPertemuan(pertemuan)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to create pertemuan: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun updatePertemuan(id: Long, pertemuan: PertemuanDTO): ApiResult<PertemuanDTO> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updatePertemuan(id, pertemuan)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to update pertemuan: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun deletePertemuan(id: Long): ApiResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deletePertemuan(id)

            when (response.code()) {
                204 -> ApiResult.Success(Unit)
                403 -> {
                    ApiResult.Error(Exception("Akses ditolak. Pastikan Anda memiliki izin yang sesuai."))
                }
                404 -> ApiResult.Error(Exception("Pertemuan tidak ditemukan"))
                else -> ApiResult.Error(Exception("Gagal menghapus pertemuan: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun getStatusByMahasiswaAndPertemuan(mahasiswaId: Long, pertemuanId: Long): ApiResult<List<StatusPertemuanDTO>> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStatusByMahasiswa(mahasiswaId)
                if (response.isSuccessful) {
                    response.body()?.let { statusList ->
                        val filteredList = statusList.filter { it.pertemuanId == pertemuanId }
                        ApiResult.Success(filteredList)
                    } ?: ApiResult.Error(Exception("Empty response body"))
                } else {
                    ApiResult.Error(Exception("Failed to fetch status: ${response.code()}"))
                }
            } catch (e: Exception) {
                ApiResult.Error(e)
            }
        }

    suspend fun getStatusByPertemuan(pertemuanId: Long): ApiResult<List<StatusPertemuanDTO>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStatusByPertemuan(pertemuanId)

            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    ApiResult.Success(it)
                } ?: ApiResult.Error(Exception("Empty response body"))
            } else {
                ApiResult.Error(Exception("Failed to fetch status: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}