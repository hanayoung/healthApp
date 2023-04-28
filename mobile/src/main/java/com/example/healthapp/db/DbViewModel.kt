package com.example.healthapp.db

import android.util.Log
import androidx.lifecycle.*

import com.example.healthapp.db.entity.HrEntity
import com.example.healthapp.db.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DbViewModel() : ViewModel() {

    private var _hrList = MutableLiveData<List<HrEntity>>()
    val hrList : LiveData<List<HrEntity>>
        get() =_hrList

    private val repository = Repository()

    fun getData() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("MainViewModel", repository.getHrList().toString())
        _hrList.postValue(repository.getHrList())
    }

    fun insertData(value:Int,time:String) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertHrData(value,time)
    }

    fun removeAll()=viewModelScope.launch(Dispatchers.IO) {
        repository.deleteHrList()
    }

}