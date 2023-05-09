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
fun TempSensorApp(
    sensorManager: SensorManager,
    dataClient: DataClient,
    context: Context
) {
    HealthAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            timeText = { TimeText() }
        ) {

            val viewModel: TempSensorViewModel = viewModel(
                factory = TempSensorViewModelFactory(
                    context = context,
                    dataClient = dataClient
                )
            )
            val tempEnabled by viewModel.enabled.collectAsState()
            TempSensorScreen(
                context=context,
                sensorManager = sensorManager,
                tempEnabled = tempEnabled,
//                    hrEnabled = hrEnabled,
                onButton1Click = { viewModel.toggleEnabled() },
//                    onButton2Click = { hrViewModel.toggleEnabled() },
                dataClient=dataClient
            )

        }
    }
}