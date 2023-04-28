package com.example.healthapp.presentation.mysql

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://172.30.1.29:8080/api/"

    private val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getInstance() : Retrofit {
        return client
    }

}