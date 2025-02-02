package com.example.pembelajaranmandirippk2025

import java.time.LocalDateTime

data class StatusPertemuanDTO(
    val id: Long? = null,
    val mahasiswaId: Long,
    val namaLengkapMahasiswa: String,
    val pertemuanId: Long,
    val statusMateri: String,
    val linkPengerjaanPraktikum: String? = null,
    val statusPengumpulan: String,
    val statusKuis: String,
    val skorPraktikum: Int? = null,
    val skorKuis: Int? = null,
)