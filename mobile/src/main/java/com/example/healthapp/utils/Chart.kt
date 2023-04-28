package com.example.healthapp

import android.graphics.Color
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.healthapp.db.DbViewModel
import com.example.healthapp.db.entity.HrEntity
import com.example.healthapp.view.hr.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var hrInput : Array<Double> ?= null
var lightInput : Array<Double> ?= null

fun addEntry(lineChart:LineChart, type:String, viewModel: MainViewModel, lifecycleScope: LifecycleCoroutineScope){

    var temp:Double = when(type){
        "hr" -> {
            viewModel.hr.value!!.toDouble()
        }
        "light" ->{
            viewModel.light.value!!.toDouble()
        }
        else ->{
            0.0
        }
    }
    if(hrInput!=null&&type=="hr"){ // lateinit한 input값이 초기화되어있는지 확인
        hrInput= hrInput!!.plus(temp) // input이 초기화되어있으니 요소를 추가
    }else if(lightInput!=null&&type=="light"){
        lightInput= lightInput!!.plus(temp)
    }else{
        val entries = ArrayList<Entry>()
        // Entry 배열 초기값 입력
        entries.add(Entry(0F, 0F))
        // 그래프 구현을 위한 LineDataSet 생성
        val dataset = LineDataSet(entries, "input")
        // 그래프 data 생성 -> 최종 입력 데이터
        val data = LineData(dataset)
        // chart.xml에 배치된 lineChart에 데이터 연결
        lineChart.data = data

        // 그래프 생성
        lineChart.animateXY(1, 1)
        when(type){
            "hr" ->{
                hrInput=Array<Double>(1,{temp})
            }
            "light" -> {
                lightInput=Array<Double>(1,{temp})
            }
        } // input 초기화 작업
    }

    lifecycleScope.launch {
        val data = lineChart.data // 연결된 데이터 가져옴
        val dataSet = data.getDataSetByIndex(0) // 0번째 위치의 데이터셋 가져옴
        if(type=="hr"){
            for (i in hrInput!!.indices) {
                if (i < dataSet.entryCount) { // 기존 dataset에 있는 값은 추가할 필요가 없으므로 분기처리
                    dataSet.getEntryForIndex(i).y = hrInput!![i].toFloat() // dataset에서 값 가져옴
                } else {
                    delay(100)
                    dataSet.addEntry(Entry(i.toFloat(), hrInput!![i].toFloat()))
                    data.notifyDataChanged()
                }
                lineChart.notifyDataSetChanged()
                lineChart.setVisibleXRangeMaximum(4f)
                lineChart.moveViewToX(dataSet.entryCount.toFloat())
                lineChart.invalidate()
            }
        }else if(type=="light"){
            for (i in lightInput!!.indices) {
                if (i < dataSet.entryCount) { // 기존 dataset에 있는 값은 추가할 필요가 없으므로 분기처리
                    dataSet.getEntryForIndex(i).y = lightInput!![i].toFloat() // dataset에서 값 가져옴
                } else {
                    delay(100)
                    dataSet.addEntry(Entry(i.toFloat(), lightInput!![i].toFloat()))
                    data.notifyDataChanged()
                }
                lineChart.notifyDataSetChanged()
                lineChart.setVisibleXRangeMaximum(4f)
                lineChart.moveViewToX(dataSet.entryCount.toFloat())
                lineChart.invalidate()
            }
        }
    }
}

fun addStatEntry(lineChart:LineChart, hrDataList:ArrayList<HrEntity>, lifecycleScope: LifecycleCoroutineScope){
    var input = hrDataList
        val entries = ArrayList<Entry>()
        // Entry 배열 초기값 입력
        entries.add(Entry(0F, 0F))
        // 그래프 구현을 위한 LineDataSet 생성
        val dataset = LineDataSet(entries, "input")
        // 그래프 data 생성 -> 최종 입력 데이터
        val data = LineData(dataset)
        // chart.xml에 배치된 lineChart에 데이터 연결
        lineChart.data = data
        lineChart.axisLeft.gridColor=Color.TRANSPARENT
        lineChart.axisRight.gridColor = Color.TRANSPARENT
        lineChart.xAxis.gridColor = Color.TRANSPARENT

        // 그래프 생성
        lineChart.animateXY(1, 1)

    lifecycleScope.launch {
        val data = lineChart.data // 연결된 데이터 가져옴
        val dataSet = data.getDataSetByIndex(0) // 0번째 위치의 데이터셋 가져옴
            for (i in input!!.indices) {
                if (i < dataSet.entryCount) { // 기존 dataset에 있는 값은 추가할 필요가 없으므로 분기처리
                    dataSet.getEntryForIndex(i).y = input!![i].value.toFloat() // dataset에서 값 가져옴
                } else {
                    delay(100)
                    dataSet.addEntry(Entry(i.toFloat(), input!![i].value.toFloat()))
                    data.notifyDataChanged()
                }
                lineChart.notifyDataSetChanged()
                lineChart.setVisibleXRangeMaximum(4f)
                lineChart.moveViewToX(dataSet.entryCount.toFloat())
                lineChart.invalidate()
            }
    }
}













//private fun setChart(lineChart: LineChart){
//    val xAxis : XAxis = lineChart.xAxis
//
//    xAxis.apply {
//        position=XAxis.XAxisPosition.BOTTOM
//        textSize=10f
//        setDrawGridLines(false)
//        granularity=1f
//        axisMinimum=2f
//        isGranularityEnabled = true
//    }
//    lineChart.apply {
//        axisRight.isEnabled = false
//        axisLeft.axisMaximum = 50f
//        legend.apply {
//            textSize=15f
//            verticalAlignment= Legend.LegendVerticalAlignment.TOP
//            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
//            orientation = Legend.LegendOrientation.HORIZONTAL
//            setDrawInside(false)
//        }
//    }
//    val lineData = LineData()
//    lineChart.data=lineData
//    feedMultiple()
//
//}
//
//
//private fun feedMultiple(lineChart: LineChart){
////    if(thread !== null){
////        thread!!.interrupt()
////    }
//    val runnable = Runnable{
//        addEntry(lineChart =lineChart )
//    }
//    Thread(Runnable {
//        while (true){
//            runOnUiThread(runnable)
//            try {
//                Thread.sleep(1000)
//            }
//            catch (e: InterruptedException){
//                e.printStackTrace()
//            }
//        }
//    })!!.start()
//}
//
//
//private fun addEntry(lineChart: LineChart){
//    val data : LineData = lineChart.data
//
//    data ?.let{
//        var set : ILineDataSet? = data.getDataSetByIndex(0)
//
//        if(set == null){
//            set = createSet()
//
//        }
//    }
//}
