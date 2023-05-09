package com.example.healthapp.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.healthapp.MyForegroundService
import com.example.healthapp.dataModel.ActivityModel
import com.example.healthapp.adapter.ActivityRVAdapter
import com.example.healthapp.R
import com.example.healthapp.adapter.ViewPagerAdapter
import com.example.healthapp.databinding.ActivityMainBinding
import com.example.healthapp.db.DbViewModel
import com.example.healthapp.view.TensorActivity
import com.example.healthapp.view.hr.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var rvAdapter : ActivityRVAdapter
    private lateinit var binding: ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private val dbViewModel : DbViewModel by viewModels()
    private var isServiceRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = ArrayList<ActivityModel>()

        val viewPager = findViewById<ViewPager2>(R.id.pager)
        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout,binding.pager){tab,position ->

        }.attach()

        rvAdapter = ActivityRVAdapter(this,items)

            val temp1 = ActivityModel("5,050","5,950",1)
            val temp2 = ActivityModel("8","55",2)
            val temp3 = ActivityModel("90","none",3)
            val temp4 = ActivityModel("none","none",4)
            items.add(temp1!! as ActivityModel)
            items.add(temp2!! as ActivityModel)
            items.add(temp3!! as ActivityModel)
            items.add(temp4!! as ActivityModel)
            Log.d("items",items.toString())
            Log.d("items", (items[3].type).javaClass.toString())
            rvAdapter.notifyDataSetChanged()


        val rv : RecyclerView = findViewById(R.id.recyclerView)

        rv.adapter = rvAdapter
        rv.layoutManager = GridLayoutManager(this,1)

        val toggleBtn = findViewById<ToggleButton>(R.id.toggleButton)

        // Set up the foreground service intent
        val foregroundServiceIntent = Intent(this, MyForegroundService::class.java)

        // Set up the toggle button click listener
        toggleBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Start the foreground service
                foregroundServiceIntent.action="START"
                startService(foregroundServiceIntent)
            } else {
                // Stop the foreground service
                foregroundServiceIntent.action="STOP"
                startService(foregroundServiceIntent)
            }
        }
        binding.tensorTest.setOnClickListener {
            val intent = Intent(this,TensorActivity::class.java)
            startActivity(intent)
        }

//        viewModel.hr.observe(this, Observer {
//            val date = SimpleDateFormat("yyyy-MM-dd-hh-mm").format(System.currentTimeMillis())
//            dbViewModel.insertData(viewModel.hr.value!!.toInt(),date)
//        })

    }
}