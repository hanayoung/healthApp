package com.example.healthapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.healthapp.db.repository.Repository
import com.example.healthapp.mysql.RetrofitInstance
import com.example.healthapp.mysql.SqlRepository
import com.example.healthapp.mysql.api.HrApi
import com.example.healthapp.mysql.model.Hr
import com.example.healthapp.view.main.MainActivity
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.POST
import java.text.SimpleDateFormat

class MyForegroundService : Service(), DataClient.OnDataChangedListener {
    private lateinit var notificationManager: NotificationManager
    private lateinit var dataClient: DataClient
    lateinit var job : Job
    private val repository = Repository()
    private val heartRateLiveData = MutableLiveData<Int>()
    private val sqlRepository = SqlRepository()
    private val client = RetrofitInstance.getInstance().create(HrApi::class.java)

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "ForegroundServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()

        dataClient = Wearable.getDataClient(this)
        dataClient.addListener(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            "START" -> {
                Log.d("onStartCommand","start")
                job = CoroutineScope(Dispatchers.Main).launch {
                    Log.d("onStartCommand","job")
                    startForeground(NOTIFICATION_ID,createNotification())
                }
            }
            "STOP" -> {
                try {
                    job.cancel()
                    stopForeground(true)
                    stopSelf()
                }catch (e : java.lang.Exception){

                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        dataClient.removeListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
            try {
                Log.d("onDataChanged","onDatachanged")
                if (!dataEventBuffer.isClosed) {
                    Log.d("dataeventbuffer","dataeventbuffer")
                    if(dataEventBuffer.count > 0){
                       Log.d("dataeventbuffer","count>0")

                            dataEventBuffer.forEach { event ->
                                if (event.type == DataEvent.TYPE_CHANGED) {
                                    val item = event.dataItem
                                    Log.d("datachanged", item.toString())
                                    if (item.uri.path?.compareTo("/heart_rate") == 0) {
                                        val dataMap = DataMapItem.fromDataItem(item).dataMap
                                        val myData = dataMap.getInt("heart_rate_key")
                                        Log.d("MyForegroundService", "Received data: $myData")

                                }
                            }
                        }
                    }
                }
                else{
                    Log.d("dataeventbuffer","isClosed")
                }
            } catch (e: Exception) {
                Log.e("MyForegroundService", "Error: ${e.message}")
            } finally {
                dataEventBuffer.release()
            }

    }

    private fun createNotification(): Notification {
            Log.d("createNotification","1")
            val notificationIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        Log.d("createNotification","2")
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        Log.d("createNotification","3")

            val builder = NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("Foreground Service")
                .setContentText("Listening for data changes")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        Log.d("createNotification","4")
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Log.d("createNotification","5")
                val name = "name"
                val descriptionText = "descriptionText"
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel("CHANNEL_ID",name,importance).apply {
                    description = descriptionText
                }
                Log.d("createNotification","6")
                val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                Log.d("createNotification","7")
                try {
                    notificationManager.createNotificationChannel(channel)
                    Log.d("createNotification","8")
                } catch (e: Exception) {
                    Log.e("exceptions", "Error creating notification channel: ${e.message}")
                }
            }
            return builder.build()
        }
    private fun insert(value :Int, time: String){
        val requestData = Hr(value,time)

        client.insertHr(requestData).enqueue(object : Callback<Hr>{
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
