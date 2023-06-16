package com.noga.guard.networking

data class GempaResponse(
    val infogempa: Infogempa
)

data class Infogempa (
    val gempa: List<Gempa>
)

data class Gempa (
    val tanggal: String,
    val jam: String,
    val dateTime: String,
    val coordinates: String,
    val lintang: String,
    val bujur: String,
    val magnitude: String,
    val kedalaman: String,
    val wilayah: String,
    val potensi: Potensi
)

enum class Potensi {
    TidakBerpotensiTsunami
}
