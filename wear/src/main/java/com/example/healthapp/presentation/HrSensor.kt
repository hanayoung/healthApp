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


fun hrFlow(context: Context): Flow<Float> {
    var hr : Sensor?= null
    var sensorManager : SensorManager = getSystemService(context, SensorManager::class.java)!!
    hr = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    return callbackFlow<Float> {
        val hrEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Do nothing
            }

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
                    trySend(event.values[0]).isSuccess
                }
            }
        }
        sensorManager.registerListener(
            hrEventListener,
            hr,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        awaitClose { sensorManager.unregisterListener(hrEventListener) }
    }
}

//@Composable
//fun HrSensor(
//    context:Context
//){
//    var hr : Sensor ?= null
//    val viewModel : HrViewModel = viewModel()
//    var sensorManager : SensorManager = getSystemService(context, SensorManager::class.java)!!
//    hr = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
//
//    val sensorStatus = remember {
//        mutableStateOf("")
//    }
//    DisposableEffect(sensorManager, hr) {
//        val hrEventListener = object : SensorEventListener {
//            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
//                Log.d("sensorManager","in onaccuracychanged")
//            }
//
//            override fun onSensorChanged(event: SensorEvent) {
//                Log.d("sensorManager","in onsensorchanged")
//                if (event.sensor.type == Sensor.TYPE_HEART_RATE) {
//                    sensorStatus.value = event.values[0].toString()
//                    Log.d("sensorManager",event.values[0].toString())
//                    viewModel.hr.value=event.values[0].toDouble()
//                    Log.d("sensorManager viewModel",viewModel.hr.value.toString())
//                }
//            }
//        }
//
//        // on below line we are registering listener for our sensor manager.
//        sensorManager.registerListener(
//            hrEventListener,
//            hr,
//            SensorManager.SENSOR_DELAY_FASTEST
//        )
//        onDispose {
//            sensorManager.unregisterListener(hrEventListener)
//        }
//    }
//    // on below line we are creating a column
//    Column(
//        // on below line we are specifying modifier
//        // and setting max height and max width for our column
//        modifier = Modifier
//            .fillMaxSize()
//            .fillMaxHeight()
//            .fillMaxWidth()
//            // on below line we are
//            // adding padding for our column
//            .padding(5.dp),
//        // on below line we are specifying horizontal
//        // and vertical alignment for our column
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        // on below line we are creating a simple text
//        // in which we are displaying a text as Object is
//        Text(
//            "HR is", Modifier.padding(5.dp), Color.Black, 40.sp,
//            // on below line we are setting text color
//
//            // on below line we are specifying font weight
//            fontWeight = FontWeight.Bold,
//
//            // on below line we are specifying font family.
//            fontFamily = FontFamily.Default
//
//            // on below line we are specifying
//            // font size and padding from all sides.
//        )
//        // on below line we are creating a text for displaying
//        // sensor status weather object is near or away
//        Text(
//            text = sensorStatus.value,
//            // on below line we are setting color for our text
//            color = Color.Black,
//
//            // on below line we are setting font weight as bold
//            fontWeight = FontWeight.Bold,
//
//            // on below line we are setting font family
//            fontFamily = FontFamily.Default,
//
//            // on below line we are setting font family and padding
//            fontSize = 40.sp, modifier = Modifier.padding(5.dp)
//        )
//        // on below line we are creating a text for displaying a sensor.
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
//    }
//}
