package com.example.healthapp.mysql.api

import android.graphics.Bitmap
import com.example.healthapp.mysql.model.Hr
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HrApi {
    @POST("insert")
    fun insertHr(@Body hrData:Hr): Call<Hr>

    @GET("get")
    fun getAllData() : Call<List<Hr>>

}