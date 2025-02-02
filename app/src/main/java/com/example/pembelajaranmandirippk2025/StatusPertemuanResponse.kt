package com.example.pembelajaranmandirippk2025

import java.time.LocalDateTime

data class StatusPertemuanResponse(
    val id: Long,
    val mahasiswaId: Long,
    val pertemuanId: Long,
    val statusMateri: String,
    val tanggalStatusMateri: LocalDateTime?,
    val linkPengerjaanPraktikum: String?,
    val statusPengumpulan: String,
    val tanggalStatusPengumpulan: LocalDateTime?,
    val skorPraktikum: Int?,
    val statusKuis: String,
    val tanggalStatusKuis: LocalDateTime?,
    val skorKuis: Int?
)
