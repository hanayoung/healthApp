package com.example.healthapp.view.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.healthapp.adapter.ViewPagerAdapter
import com.example.healthapp.databinding.FragmentBlank2Binding
import com.example.healthapp.db.DbViewModel
import com.example.healthapp.db.entity.HrEntity
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BlankFragment2 : Fragment() {

    private lateinit var adapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    private var _binding : FragmentBlank2Binding? = null
    private val binding get() = _binding!!
    private lateinit var dbViewModel : DbViewModel
    private lateinit var input : MutableList<HrEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBlank2Binding.inflate(inflater, container, false)
        dbViewModel = ViewModelProvider(this)[DbViewModel::class.java]



        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var input : Array<Int> = arrayOf(4,22,52,34,12,32)
        val entries = ArrayList<Entry>()
        // Entry 배열 초기값 입력
        entries.add(Entry(0F, 0F))
        // 그래프 구현을 위한 LineDataSet 생성
        val dataset = LineDataSet(entries, "input")
        // 그래프 data 생성 -> 최종 입력 데이터
        val data = LineData(dataset)
        // chart.xml에 배치된 lineChart에 데이터 연결
        binding.linechart.data = data
        binding.linechart.axisLeft.gridColor = Color.TRANSPARENT
        binding.linechart.axisRight.gridColor = Color.TRANSPARENT; // 두 줄을 통해서 배경 가로 격자 투명하게
        binding.linechart.xAxis.gridColor = Color.TRANSPARENT // 배경 세로 격자 투명하게


        // 그래프 생성
        binding.linechart.animateXY(1, 1)
        dataset.mode = LineDataSet.Mode.CUBIC_BEZIER // 그래프 둥글게
        binding.linechart.setDrawGridBackground(false)

        lifecycleScope.launch {
            val data = binding.linechart.data // 연결된 데이터 가져옴
            val dataSet = data.getDataSetByIndex(0) // 0번째 위치의 데이터셋 가져옴
            for (i in input.indices) {
                if (i < dataSet.entryCount) { // 기존 dataset에 있는 값은 추가할 필요가 없으므로 분기처리
                    dataSet.getEntryForIndex(i).y = input[i].toFloat() // dataset에서 값 가져옴
                } else {
                    delay(100)
                    dataSet.addEntry(Entry(i.toFloat(), input[i].toFloat()))
                    data.notifyDataChanged()
                }
                binding.linechart.notifyDataSetChanged()
                binding.linechart.setVisibleXRangeMaximum(4f)
                binding.linechart.moveViewToX(dataSet.entryCount.toFloat())
                binding.linechart.invalidate()
            }
        }
    }
}