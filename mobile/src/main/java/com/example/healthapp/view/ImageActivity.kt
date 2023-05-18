package com.example.healthapp.view

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthapp.R
import com.example.healthapp.mysql.RetrofitInstance
import com.example.healthapp.mysql.api.ImgApi
import kotlinx.coroutines.*

class ImageActivity : AppCompatActivity() {
    private val client = RetrofitInstance.getInstance().create(ImgApi::class.java)
    private val imageLoaderScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val img = findViewById<ImageView>(R.id.getImage)
//        val userName = "tester"
        val userName = "catester"
        imageLoaderScope.launch(Dispatchers.IO) {
            try {
                val response = client.getImg(userName).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let { body ->
                        val inputStream = body.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)

                        // Update the ImageView on the main thread
                        withContext(Dispatchers.Main) {
                            img.setImageBitmap(bitmap)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ImageActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.d("testt", e.message.toString())
            }
//            client.getImg(userName).enqueue(object: Callback<ResponseBody> {
//                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                    if(response.isSuccessful){
//                        Log.d("isSuccessful?",response.message())
//                        Log.d("response",response.headers().toString())
//                        val responseBody = response.body()?.byteStream()
//                        Log.d("is", responseBody.toString())
//                        val bitmap = BitmapFactory.decodeStream(responseBody)
//                        Log.d("bitmap",bitmap.toString())
//                    val bytes = ByteArrayOutputStream()
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//                    val path: String = MediaStore.Images.Media.insertImage(
//                        contentResolver,
//                        bitmap,
//                        "Title",
//                        null
//                    )
//                    Log.d("path",path)
//                    val absPath = absolutelyPath(Uri.parse(path),this@ImageActivity)
//                    Log.d("absPath",absPath)
//                    val rotatedBitmap : Bitmap = rotatedBitmap(bitmap,absPath)!!
//                        img.setImageBitmap(bitmap)
//                    Glide.with(this@ImageActivity)
//                        .load(response.body().)
//                        .into(img)
//
//                    }else{
//                        Log.d("???",response.body().toString())
//                        Log.d("???",response.message())
//                        Log.d("???",response.errorBody().toString())
//                        Toast.makeText(this@ImageActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                    Log.d("testt", t.message.toString())
//                }
//            })
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        imageLoaderScope.cancel() // Cancel the CoroutineScope to avoid leaks
    }
    fun getOrientationOfImage(filepath : String): Int? {
        var exif : ExifInterface? = null
        var result: Int? = null

        try{
            exif = ExifInterface(filepath)
        }catch (e: Exception){
            e.printStackTrace()
            return -1
        }

        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
        if(orientation != -1){
            result = when(orientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
        return result
    }

    private fun rotatedBitmap(bitmap: Bitmap?, filepath: String): Bitmap? {
        val matrix = Matrix()
        var resultBitmap : Bitmap? = null

        when(getOrientationOfImage(filepath)){
            0 -> matrix.setRotate(0F)
            90 -> matrix.setRotate(90F)
            180 -> matrix.setRotate(180F)
            270 -> matrix.setRotate(270F)
        }

        resultBitmap = try{
            bitmap?.let { Bitmap.createBitmap(it, 0, 0, bitmap.width, bitmap.height, matrix, true) }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
        return resultBitmap
    }
}