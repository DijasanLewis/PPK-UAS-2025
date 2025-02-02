package com.example.pembelajaranmandirippk2025

data class UserResponse(
    val id: Long,
    val email: String,
    val role: String,
    val namaLengkap: String,
    val nidn: String? = null,  // null jika bukan dosen
    val nim: String? = null,  // null jika bukan mahasiswa
    val kelas: String? = null // null jika bukan dosen
)
