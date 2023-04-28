package com.example.healthapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthapp.db.entity.HrEntity

@Dao
interface HrDao{
    @Query("SELECT * FROM hr_table")
    fun getAllData() : List<HrEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(hr : HrEntity)

    @Query("DELETE FROM hr_table")
    fun deleteAllData()
}