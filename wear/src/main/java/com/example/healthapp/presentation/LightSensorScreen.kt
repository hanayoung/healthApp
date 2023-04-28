package com.example.healthapp.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text
import com.example.healthapp.R
import com.example.healthapp.presentation.HrViewModel
import com.example.healthapp.presentation.HrViewModelFactory
import com.example.healthapp.presentation.LightSensorViewModel
import com.example.healthapp.presentation.LightSensorViewModelFactory
import com.google.android.gms.wearable.DataClient

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LightSensorScreen(
context: Context,
sensorManager: SensorManager,
lightEnabled: Boolean,
//hrEnabled: Boolean,
onButton1Click: () -> Unit,
//onButton2Click: () -> Unit,
dataClient : DataClient
) {
    val viewModel: LightSensorViewModel = viewModel(
        factory = LightSensorViewModelFactory(
            context = context,
            dataClient = dataClient
        )
    )
    val hrViewModel : HrViewModel = viewModel(
        factory = HrViewModelFactory(
            context = context,
            dataClient = dataClient
        )
    )
    // on below line we are specifying theme as scaffold.
//    Scaffold(
//        // in scaffold we are specifying top bar.
//        topBar = {
//            // inside top bar we are specifying background color.
//            TopAppBar(
//                // along with that we are specifying title for our top bar.
//                title = {
//                    // in the top bar we are specifying tile as a text
//                    Text(
//                        // on below line we are specifying
//                        // text to display in top app bar.
//                        text = "Proximity Sensor Example",
//
//                        // on below line we are specifying
//                        // modifier to fill max width.
//                        modifier = Modifier.fillMaxWidth(),
//
//                        // on below line we are
//                        // specifying text alignment.
//                        textAlign = TextAlign.Center,
//
//                        // on below line we are
//                        // specifying color for our text.
//                        color = Color.White
//                    )
//                }
//            )
//        }
//    ) {
//        // on below line we are calling proximity
//        // sensor method to use proximity sensor.
////        Button(
////            modifier = Modifier.fillMaxWidth(0.5f),
////            onClick = {
////                Log.d("permission", "is granted")
////                onButton1Click()
////            }
////        ) {
////            val buttonTextId = if (lightEnabled) {
////                R.string.stop
////            } else {
////                R.string.start
////            }
////            Text(stringResource(buttonTextId))
////        }
////        LightSensor(context = context)
//    }

    // on below line we are creating a column
    Column(
        // on below line we are specifying modifier
        // and setting max height and max width for our column
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            // on below line we are
            // adding padding for our column
            .padding(5.dp),
        // on below line we are specifying horizontal
        // and vertical alignment for our column
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // on below line we are creating a simple text
        // in which we are displaying a text as Object is
//        Button(
//            modifier = Modifier.fillMaxWidth(0.5f),
//            onClick = {
//                Log.d("permission", "is granted")
//                onButton2Click()
//            }
//        ) {
//            val hrButtonTextId = if (hrEnabled) {
//                R.string.stop
//            } else {
//                R.string.start
//            }
//            Text(stringResource(hrButtonTextId))
//        }
        Text(
//                "Object is", Modifier.padding(5.dp), Color.Black, 40.sp,
            // on below line we are setting text color
//            text = hr.toString(),
            text = viewModel.light.value.toString(),

            // on below line we are specifying font weight
            fontWeight = FontWeight.Bold,

            // on below line we are specifying font family.
            fontFamily = FontFamily.Default,

            fontSize = 40.sp, modifier = Modifier.padding(5.dp)
            // on below line we are specifying
            // font size and padding from all sides.
        )
        Button(
            modifier = Modifier.fillMaxWidth(0.5f),
            onClick = {
                Log.d("permission", "is granted")
                onButton1Click()
            }
        ) {
            val buttonTextId = if (lightEnabled) {
                R.string.stop
            } else {
                R.string.start
            }
            Text(stringResource(buttonTextId))
        }
        // on below line we are creating a text for displaying
        // sensor status weather object is near or away
        Text(
//            text = sensorStatus.value,
            text = hrViewModel.hr.value.toString(),
            // on below line we are setting color for our text

            // on below line we are setting font weight as bold
            fontWeight = FontWeight.Bold,

            // on below line we are setting font family
            fontFamily = FontFamily.Default,

            // on below line we are setting font family and padding
            fontSize = 40.sp, modifier = Modifier.padding(5.dp)
        )
    }
}