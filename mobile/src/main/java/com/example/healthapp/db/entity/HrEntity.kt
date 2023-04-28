package com.example.healthapp.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hr_table")
data class HrEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id : Int,
    @ColumnInfo(name="value")
    var value : Int,
    @ColumnInfo(name="time")
    var time : String
        )