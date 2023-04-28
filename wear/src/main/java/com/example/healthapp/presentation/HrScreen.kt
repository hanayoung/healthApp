package com.example.healthapp.presentation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HrScreen(
    context: Context
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Proximity Sensor Example",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            )
        }
    ) {
        // on below line we are calling proximity
        // sensor method to use proximity sensor.
//        HrSensor(context = context)
    }
}