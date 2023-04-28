package com.example.healthapp.presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.services.client.data.DataTypeAvailability
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

class LightSensorViewModel(
    context:Context,
    dataClient: DataClient
) : ViewModel() {
        val enabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val light: MutableState<Double> = mutableStateOf(0.0)
        val availability: MutableState<DataTypeAvailability> =
        mutableStateOf(DataTypeAvailability.UNKNOWN)
    init {
        viewModelScope.launch {
            enabled.collect {
                if (it) {
                    lightSensorFlow(context)
                        .takeWhile { enabled.value }
                        .collect{
                            measureData ->
                            Log.d("measureData",measureData.toString())
                            light.value = measureData.toDouble()
                            updateLightSensorData(dataClient = dataClient,light.value)
                        }
                }
            }
        }
    }
    fun updateLightSensorData(dataClient: DataClient, light: Double){
        viewModelScope.launch(Dispatchers.IO) {
            val putDataMapRequest = PutDataMapRequest.create("/light")
            putDataMapRequest.dataMap.putInt("light_key", light.toInt())
            val putDataReq = putDataMapRequest.asPutDataRequest()
            putDataReq.setUrgent()
            val putDataTask = dataClient.putDataItem(putDataReq)
            try {
                Tasks.await(putDataTask).apply {
                    Log.d("UpdateLight in apply",light.toString())
                }
            } catch (e: ExecutionException) {
                Log.d("UpdateLight", "updateCalories: Failure ${e.printStackTrace()}")
            } catch (e: InterruptedException) {
                Log.d("UpdateLight", "updateCalories: Failure ${e.printStackTrace()}")
            }
        }
    }
    fun toggleEnabled() {
        enabled.value = !enabled.value
        if (!enabled.value) {
            availability.value = DataTypeAvailability.UNKNOWN
        }
    }
}

class LightSensorViewModelFactory(
    private val context: Context,
    private val dataClient: DataClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LightSensorViewModel::class.java)) {
            return LightSensorViewModel(context, dataClient = dataClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
