package com.example.healthapp.view

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.healthapp.R
import com.example.healthapp.mysql.RetrofitInstance
import com.example.healthapp.mysql.api.ImgApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImageActivity : AppCompatActivity() {
    private val client = RetrofitInstance.getInstance().create(ImgApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val img = findViewById<ImageView>(R.id.getImage)

        client.getImg().enqueue(object: Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    val responseBody = response.body()?.byteStream()
                    Log.d("is", responseBody.toString())
                    val bitmap = BitmapFactory.decodeStream(responseBody)
                    Log.d("bitmap",bitmap.toString())
                    img.setImageBitmap(bitmap)
//                    Glide.with(this@ImageActivity)
//                        .load(response.body().)
//                        .into(img)
//
                }else{
                    Log.d("???",response.body().toString())
                    Log.d("???",response.message())
                    Log.d("???",response.errorBody().toString())
                    Toast.makeText(this@ImageActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })

    }
}