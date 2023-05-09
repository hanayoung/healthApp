package com.example.healthapp.view.hr

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import androidx.lifecycle.viewModelScope
import com.example.healthapp.db.repository.Repository
import com.example.healthapp.mysql.RetrofitInstance
import com.example.healthapp.mysql.api.HrApi
import com.example.healthapp.mysql.model.Hr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class MainViewModel : ViewModel(){

    private val repository = Repository()
    private val client = RetrofitInstance.getInstance().create(HrApi::class.java)

    private val _hr = MutableLiveData<Int>()
    val hr : LiveData<Int>
        get() = _hr

    private val _light = MutableLiveData<Int>()
    val light : LiveData<Int>
    get() = _light

    private val _hrList = MutableLiveData<List<Hr>>()
    val hrList : LiveData<List<Hr>>
    get() = _hrList

    private fun insertData(value:Int, time:String) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertHrData(value,time)
    }

    fun getData()  {
            client.getAllData().enqueue(object : Callback<List<Hr>> {
                override fun onResponse(call: Call<List<Hr>>, response: Response<List<Hr>>) {
                    Log.d("APIPhone",response.body().toString())

                }

                override fun onFailure(call: Call<List<Hr>>, t: Throwable) {
                    Log.d("APIPhone","fail")
                    Log.d("APIPhone",t.message.toString())
                }
            })
        }


}