package com.example.healthapp.db.repository

import com.example.healthapp.App
import com.example.healthapp.db.db.HrDatabase
import com.example.healthapp.db.entity.HrEntity

class Repository{

    val context = App.context()
    private val db = HrDatabase.getDatabase(context)

    fun getHrList() = db.HrDao().getAllData()

    fun insertHrData(value:Int,time:String) = db.HrDao().insert(HrEntity(0,value,time))

    fun deleteHrList() = db.HrDao().deleteAllData()
}