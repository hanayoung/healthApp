package com.example.healthapp

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class MySQLHelper {
    companion object {
        private const val DATABASE_HOST = "127.0.0.1"
        private const val DATABASE_PORT = "3306"
        private const val DATABASE_NAME = "new_schema"
        private const val DATABASE_USERNAME = "root"
        private const val DATABASE_PASSWORD = "gksdkdud"

        @Throws(SQLException::class)
        fun getConnection(): Connection {
//            val url = "jdbc:mysql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME?useSSL=false"
            val url = "jdbc:mysql://127.0.0.1:3306/new_schema?useSSL=false"
            return DriverManager.getConnection(url, DATABASE_USERNAME, DATABASE_PASSWORD)
        }

//        @Throws(SQLException::class)
//        fun insertData(value: Int, time: String) {
//            val query = "INSERT INTO hr_table (value, time) VALUES (?, ?)"
//            val statement: PreparedStatement = getConnection().prepareStatement(query)
//            statement.setInt(1, value)
//            statement.setString(2, time)
//            statement.executeUpdate()
//            statement.close()
//        }

    }
}
