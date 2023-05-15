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

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.healthapp.presentation.data.HealthServicesRepository
import com.example.healthapp.presentation.data.MeasureMessage
import com.example.healthapp.presentation.mysql.RetrofitInstance
import com.example.healthapp.presentation.mysql.api.HrApi
import com.example.healthapp.presentation.mysql.model.Hr
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.concurrent.ExecutionException

class MeasureDataViewModel(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModel() {

    private val _enabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val enabled: StateFlow<Boolean> = _enabled.asStateFlow()

    val hr: MutableState<Int> = mutableStateOf(0)
    val availability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)

    private val client = RetrofitInstance.getInstance().create(HrApi::class.java)

    val uiState: MutableState<UiState> = mutableStateOf(UiState.Startup)

    init {
        viewModelScope.launch {
            val supported = healthServicesRepository.hasHeartRateCapability()
            uiState.value = if (supported) {
                UiState.Supported
            } else {
                UiState.NotSupported
            }
        }

        viewModelScope.launch {
            enabled.collect {
                if (it) {
                    healthServicesRepository.heartRateMeasureFlow()
                        .takeWhile { enabled.value }
                        .collect { measureMessage ->
                            when (measureMessage) {
                                is MeasureMessage.MeasureData -> {
                                    hr.value = measureMessage.data.last().value.toInt()
                                    val date =
                                        SimpleDateFormat("yyyy-MM-dd-hh-mm").format(System.currentTimeMillis())
                                    if(hr.value!==0){
                                        insert(hr.value,date)
                                    }
                                    Log.d("heartrate",hr.value.toString())
                                }
                                is MeasureMessage.MeasureAvailability -> {
                                    availability.value = measureMessage.availability
                                    Log.d("availability","value : ${availability.value}  availability : ${measureMessage.availability}")
                                }
                            }
                        }
                }
            }
        }
    }

    fun toggleEnabled() {
        _enabled.value = !_enabled.value
        Log.d("toggleEnabled",enabled.value.toString())
        if (!enabled.value) {
            availability.value = DataTypeAvailability.UNKNOWN
        }
    }

    private fun insert(value :Int, time: String){
        val requestData = Hr(value,time)
        Log.d("requestData",requestData.toString())

        client.insertHr(requestData).enqueue(object : Callback<Hr> {
            override fun onResponse(call: Call<Hr>, response: Response<Hr>) {
                Log.d("API1",response.body().toString())
            }
            override fun onFailure(call: Call<Hr>, t: Throwable) {
                Log.d("API1","fail")
                Log.d("API1",t.message.toString())
            }
        })
    }

}

class MeasureDataViewModelFactory(
    private val healthServicesRepository: HealthServicesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeasureDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeasureDataViewModel(
                healthServicesRepository = healthServicesRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class UiState {
    object Startup : UiState()
    object NotSupported : UiState()
    object Supported : UiState()
}
