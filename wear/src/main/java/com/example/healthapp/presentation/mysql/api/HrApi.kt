package com.example.healthapp.presentation.mysql.api

import com.example.healthapp.presentation.mysql.model.Hr
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface HrApi {
    @POST("insert")
    fun insertHr(@Body hrData: Hr): Call<Hr>
}