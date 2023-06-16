package com.noga.guard.networking

object ApiEndPoint  {
    @JvmField
    var URL_GEMPA_DIRASAKAN = "https://data.bmkg.go.id/DataMKG/TEWS/gempadirasakan.json"

    @JvmField
    var URL_GEMPA_BERPOTENSI = "https://data.bmkg.go.id/DataMKG/TEWS/autogempa.json"
    var URL_GEMPA_M5 = "https://data.bmkg.go.id/DataMKG/TEWS/gempaterkini.json"
    var URL_CUACA = "http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&appid=e3fceead41ac49070bb91a11d65576d7"
}