package com.example.healthapp.view.hr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.healthapp.R
import com.example.healthapp.addEntry
import com.example.healthapp.addStatEntry
import com.example.healthapp.databinding.ActivityHrChartBinding
import com.example.healthapp.db.DbViewModel
import com.example.healthapp.db.entity.HrEntity
import com.github.mikephil.charting.charts.LineChart
import com.google.android.gms.wearable.Wearable
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HrChartActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private val dbViewModel : DbViewModel by viewModels()
    private val dataClient by lazy { Wearable.getDataClient(this)}
    private lateinit var binding : ActivityHrChartBinding

    private val hrDataList = ArrayList<HrEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHrChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lineChart = findViewById<LineChart>(R.id.linechart)

        viewModel.hr.observe(this, Observer {
            addEntry(lineChart,"hr",viewModel,this.lifecycleScope)
//            val date = SimpleDateFormat("yyyy-MM-dd-hh-mm").format(System.currentTimeMillis())
//            dbViewModel.insertData(viewModel.hr.value!!.toInt(),date)
        }) // 여기서 관찰해서 밖에서는 감지 안되는 듯?

        binding.getListBtn.setOnClickListener {
                Log.d("edittext",binding.inputTxt.text.toString())
                val stMin = binding.inputTxt.text.toString().toIntOrNull()
                viewModel.getData()

//                dbViewModel.hrList.observe(this, Observer {
//                    if(dbViewModel.hrList.value!=null){
//                        hrDataList.clear()
//                        Log.d("activityHrChart",dbViewModel.hrList.value.toString())
//                        Log.d("curmin",LocalDateTime.now().toString())
//                        for(item in it){
//                            val dateString = item.time
//                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")
//                            val dateTime = LocalDateTime.parse(dateString, formatter)
//                            val minute = dateTime.minute
//                            val curmin=LocalDateTime.now().minute
//                            if(minute>= curmin-stMin!!&&dateTime.hour==LocalDateTime.now().hour&&dateTime.dayOfMonth==LocalDateTime.now().dayOfMonth){ //차후에 stMin뺀 값이 시간을 넘어갈 때는 따로 계산해야함
//                                hrDataList.add(item)
//                                binding.statText.text="$minute 분"
//                            }
//
//                        }
//                        Log.d("hrDataList",hrDataList.toString())
//                        addStatEntry(binding.datachart,hrDataList,this.lifecycleScope)
//                    }
//                    else{
//                        Log.d("activityHrChart","dbViewModel.hrList is null")
//                    }
//                })
            binding.deleteBtn.setOnClickListener {
                dbViewModel.removeAll()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}