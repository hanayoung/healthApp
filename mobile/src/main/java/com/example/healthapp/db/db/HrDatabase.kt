package com.example.healthapp.db.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.healthapp.db.dao.HrDao
import com.example.healthapp.db.entity.HrEntity

@Database(entities = [HrEntity::class], version = 1)
abstract class HrDatabase : RoomDatabase() {
    abstract fun HrDao() :HrDao

    companion object{
        @Volatile
        private var INSTANCE : HrDatabase? = null

        fun getDatabase(
            context: Context
        ) : HrDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                    HrDatabase::class.java,
                    "hr_database"
                        )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}