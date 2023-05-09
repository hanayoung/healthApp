package com.example.healthapp.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

    fun TempSensorFlow(context: Context): Flow<Float> {
        var temp : Sensor?= null
        var sensorManager : SensorManager = ContextCompat.getSystemService(
            context,
            SensorManager::class.java
        )!!
        temp = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        return callbackFlow<Float> {
            val tempSensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // Do nothing
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                        trySend(event.values[0]).isSuccess
                    }
                }
            }
            sensorManager.registerListener(
                tempSensorEventListener,
                temp,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            awaitClose { sensorManager.unregisterListener(tempSensorEventListener) }
        }
    }
