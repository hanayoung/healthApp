/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.healthapp.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.services.client.data.DataTypeAvailability
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.healthapp.R
import com.example.healthapp.presentation.theme.HealthAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@Composable
fun HrLabel(
    hr: Int,
    availability: DataTypeAvailability
) {
    val icon = when (availability) {
        DataTypeAvailability.AVAILABLE -> Icons.Default.Favorite
        DataTypeAvailability.ACQUIRING -> Icons.Default.Star
        DataTypeAvailability.UNKNOWN -> Icons.Default.Check
        DataTypeAvailability.UNAVAILABLE,
        DataTypeAvailability.UNAVAILABLE_DEVICE_OFF_BODY -> Icons.Default.Warning
        else -> Icons.Default.Refresh
    }
    val text = if (availability == DataTypeAvailability.AVAILABLE) {
        hr.toInt().toString()
    } else {
        stringResource(id = R.string.no_hr_reading)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.icon),
            tint = Color.Red
        )
        Text(
            text = text,
            style = MaterialTheme.typography.display1,
            color = Color.White

        )
    }
}

@ExperimentalPermissionsApi
@Preview(
    device = Devices.WEAR_OS_SMALL_ROUND,
    showBackground = false,
    showSystemUi = true
)
@Composable
fun HrLabelPreview() {
   HealthAppTheme {
        HrLabel(
            hr = 121,
            availability = DataTypeAvailability.AVAILABLE
        )
    }
}
