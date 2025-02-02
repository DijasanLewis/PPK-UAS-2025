package com.example.pembelajaranmandirippk2025.api

import com.example.pembelajaranmandirippk2025.AuthRequest
import com.example.pembelajaranmandirippk2025.AuthResponse
import com.example.pembelajaranmandirippk2025.ChangePasswordRequest
import com.example.pembelajaranmandirippk2025.PertemuanDTO
import com.example.pembelajaranmandirippk2025.StatusPertemuanDTO
import com.example.pembelajaranmandirippk2025.UserDTO
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("api/users/register")
    suspend fun register(@Body user: UserDTO): Response<UserDTO>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Long): Response<UserDTO>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: UserDTO): Response<UserDTO>

    @PUT("api/users/{id}/change-password")
    suspend fun changePassword(
        @Path("id") id: Long,
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    @GET("api/pertemuan")
    suspend fun getAllPertemuan(): Response<List<PertemuanDTO>>

    @POST("api/pertemuan")
    suspend fun createPertemuan(@Body pertemuan: PertemuanDTO): Response<PertemuanDTO>

    @GET("api/pertemuan/{id}")
    suspend fun getPertemuan(@Path("id") id: Long): Response<PertemuanDTO>

    @PUT("api/pertemuan/{id}")
    suspend fun updatePertemuan(@Path("id") id: Long, @Body pertemuan: PertemuanDTO): Response<PertemuanDTO>

    @DELETE("api/pertemuan/{id}")
    @Headers(
        "Accept: application/json",
        "X-Requested-With: XMLHttpRequest"
    )
    suspend fun deletePertemuan(@Path("id") id: Long): Response<Unit>

    @GET("api/status-pertemuan/mahasiswa/{id}")
    suspend fun getStatusByMahasiswa(@Path("id") mahasiswaId: Long): Response<List<StatusPertemuanDTO>>

    @GET("api/status-pertemuan/pertemuan/{id}")
    suspend fun getStatusByPertemuan(@Path("id") pertemuanId: Long): Response<List<StatusPertemuanDTO>>

    @PUT("api/status-pertemuan")
    suspend fun updateStatus(
        @Body status: StatusPertemuanDTO,
        @Query("userId") userId: Long
    ): Response<StatusPertemuanDTO>

    @GET("api/users/profile")
    suspend fun getCurrentUserProfile(): Response<UserDTO>
}
