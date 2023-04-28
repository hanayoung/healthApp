package com.example.healthapp.presentation

import android.content.Context
import android.hardware.SensorManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import com.example.healthapp.presentation.theme.HealthAppTheme
import com.google.android.gms.wearable.DataClient

@Composable
fun LightSensorApp(
    sensorManager: SensorManager,
    dataClient: DataClient,
    context:Context
) {
    HealthAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            timeText = { TimeText() }
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
                val lightEnabled by viewModel.enabled.collectAsState()
                val hrEnabled by hrViewModel.enabled.collectAsState()
                LightSensorScreen(
                    context=context,
                    sensorManager = sensorManager,
                    lightEnabled = lightEnabled,
//                    hrEnabled = hrEnabled,
                    onButton1Click = { viewModel.toggleEnabled() },
//                    onButton2Click = { hrViewModel.toggleEnabled() },
                    dataClient=dataClient
                )

        }
    }
}
