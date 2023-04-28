package com.example.healthapp.mysql

import com.example.healthapp.mysql.api.HrApi
import com.example.healthapp.mysql.model.Hr

class SqlRepository {
    private val client = RetrofitInstance.getInstance().create(HrApi::class.java)

    suspend fun insertHrData(value:Int,time:String) = client.insertHr(Hr(value,time))
}