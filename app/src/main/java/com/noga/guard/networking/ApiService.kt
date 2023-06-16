package com.noga.guard.networking

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("DataMKG/TEWS/gempadirasakan.json")
    fun dirasakan(): Call<GempaResponse>

    @GET("DataMKG/TEWS/autogempa.json")
    fun berpotensi():Call<GempaResponse>

    @GET("DataMKG/TEWS/gempaterkini.json")
    fun terkini(): Call<GempaResponse>

}