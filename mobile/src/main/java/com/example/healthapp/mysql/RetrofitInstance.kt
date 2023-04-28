package com.example.healthapp.mysql

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://172.30.1.85:8080/api/" // const val은 컴파일 시간 동안 할당되어야 함, 함수 같은 거 할당 x

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