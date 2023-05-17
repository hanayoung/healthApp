package com.example.healthapp.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ToggleButton
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
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
import com.example.healthapp.view.CameraActivity
import com.example.healthapp.view.ImageActivity
//import com.example.healthapp.view.TensorGalleryActivity
import com.example.healthapp.view.TensorCameraActivity
import com.example.healthapp.view.TensorGalleryActivity
import com.example.healthapp.view.hr.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var rvAdapter : ActivityRVAdapter
    private lateinit var binding : ActivityMainBinding
    private val viewModel : MainViewModel by viewModels()
    private val dbViewModel : DbViewModel by viewModels()
    private var isServiceRunning = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            0 -> {
                if (grantResults.isNotEmpty()){
                    var isAllGranted = true
                    // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }

                    // 요청한 권한을 모두 허용했음.
                    if (isAllGranted) {
                        // 다음 step으로 ~
                    }
                    // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.CAMERA)){
                            // 다시 묻지 않기 체크하면서 권한 거부 되었음.
                        } else {
                            // 접근 권한 거부하였음.
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fun requestPermissions(): Boolean {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                return true
            }

            val permissions: Array<String> = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE)

            ActivityCompat.requestPermissions(this, permissions, 0)
            return false
        } // 권한 요청

        requestPermissions()
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

        binding.cameraBtn.setOnClickListener {
            val intent = Intent(this,CameraActivity::class.java)
            startActivity(intent)
        }

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
//            val intent = Intent(this, TensorGalleryActivity::class.java)
            val intent = Intent(this,ImageActivity::class.java)
            startActivity(intent)
        }

        binding.galleryBtn.setOnClickListener {
            val intent = Intent(this, TensorGalleryActivity::class.java)
            startActivity(intent)
        }

//        viewModel.hr.observe(this, Observer {
//            val date = SimpleDateFormat("yyyy-MM-dd-hh-mm").format(System.currentTimeMillis())
//            dbViewModel.insertData(viewModel.hr.value!!.toInt(),date)
//        })

    }
}