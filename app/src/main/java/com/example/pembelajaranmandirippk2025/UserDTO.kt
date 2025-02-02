package com.example.pembelajaranmandirippk2025

data class UserDTO(
    val id: Long? = null,
    val email: String,
    val password: String,
    val role: String, // MAHASISWA atau DOSEN
    val namaLengkap: String? = null,
    val nidn: String? = null, // hanya untuk dosen
    val nim: String? = null,  // hanya untuk mahasiswa
    val kelas: String? = null  // hanya untuk dosen
)
