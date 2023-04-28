package com.example.healthapp.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


    fun lightSensorFlow(context: Context): Flow<Float> {
        var brightness : Sensor?= null
        var sensorManager : SensorManager = getSystemService(context, SensorManager::class.java)!!
        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        return callbackFlow<Float> {
            val lightSensorEventListener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // Do nothing
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_LIGHT) {
                        trySend(event.values[0]).isSuccess
                    }
                }
            }
            sensorManager.registerListener(
                lightSensorEventListener,
                brightness,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            awaitClose { sensorManager.unregisterListener(lightSensorEventListener) }
        }
    }

//fun LightSensor(
//    context:Context
//){
//        var brightness : Sensor ?= null
//        val viewModel : LightSensorViewModel = viewModel()
//        var sensorManager : SensorManager = getSystemService(context, SensorManager::class.java)!!
//        brightness = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
//        val hrViewModel : HrViewModel = viewModel()
//        val hr = hrViewModel.hr.value
//
//        val sensorStatus = remember {
//            mutableStateOf("")
//        }
//    DisposableEffect(sensorManager, brightness) {
//        val lightSensorEventListener = object : SensorEventListener {
//            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
//                Log.d("sensorManager","in onaccuracychanged")
//            }
//
//            override fun onSensorChanged(event: SensorEvent) {
//                Log.d("sensorManager","in onsensorchanged")
//                if (event.sensor.type == Sensor.TYPE_LIGHT) {
//                        sensorStatus.value = event.values[0].toString()
//                        Log.d("sensorManager",event.values[0].toString())
//                        viewModel.light.value=event.values[0].toDouble()
//                        Log.d("sensorManager viewModel",viewModel.light.value.toString())
//                    }
//            }
//        }
//        // on below line we are registering listener for our sensor manager.
//
//        sensorManager.registerListener(
//            lightSensorEventListener,
//            brightness,
//            SensorManager.SENSOR_DELAY_NORMAL
//        )
//        onDispose {
//            sensorManager.unregisterListener(lightSensorEventListener)
//        }
//    }
//        // on below line we are creating a column
////        Column(
////            // on below line we are specifying modifier
////            // and setting max height and max width for our column
////            modifier = Modifier
////                .fillMaxSize()
////                .fillMaxHeight()
////                .fillMaxWidth()
////                // on below line we are
////                // adding padding for our column
////                .padding(5.dp),
////            // on below line we are specifying horizontal
////            // and vertical alignment for our column
////            horizontalAlignment = Alignment.CenterHorizontally,
////            verticalArrangement = Arrangement.Center
////        ) {
//            // on below line we are creating a simple text
//            // in which we are displaying a text as Object is
////            Text(
//////                "Object is", Modifier.padding(5.dp), Color.Black, 40.sp,
////                // on below line we are setting text color
////                text = hr.toString(),
////                // on below line we are specifying font weight
////                fontWeight = FontWeight.Bold,
////
////                // on below line we are specifying font family.
////                fontFamily = FontFamily.Default,
////
////                fontSize = 40.sp, modifier = Modifier.padding(5.dp)
////                // on below line we are specifying
////                // font size and padding from all sides.
////            )
////            // on below line we are creating a text for displaying
////            // sensor status weather object is near or away
////            Text(
////                text = sensorStatus.value,
////                // on below line we are setting color for our text
////                color = Color.Black,
////
////                // on below line we are setting font weight as bold
////                fontWeight = FontWeight.Bold,
////
////                // on below line we are setting font family
////                fontFamily = FontFamily.Default,
////
////                // on below line we are setting font family and padding
////                fontSize = 40.sp, modifier = Modifier.padding(5.dp)
////            )
//            // on below line we are creating a text for displaying a sensor.
////            Text(
////                text = "Sensor",
////                // on below line we are displaying a text color
////                color = Color.Black,
////
////                // on below line we are setting font weight
////                fontWeight = FontWeight.Bold,
////
////                // on below line we are setting font family
////                fontFamily = FontFamily.Default,
////
////                // on below line we are setting font size and padding.
////                fontSize = 40.sp, modifier = Modifier.padding(5.dp)
////            )
////        }
//    }
