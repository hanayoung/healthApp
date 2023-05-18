package com.example.healthapp.view

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
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.example.healthapp.R
import com.example.healthapp.mysql.RetrofitInstance
import com.example.healthapp.mysql.api.ImgApi
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class TensorCameraActivity : AppCompatActivity() {
    private lateinit var img : ImageView
    private lateinit var cropImgRight : ImageView
    private lateinit var cropImgLeft : ImageView
    private lateinit var path: String
    private lateinit var resized :Bitmap
    private lateinit var filePathList : ArrayList<String>
    private val client = RetrofitInstance.getInstance().create(ImgApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensor)

        img = findViewById<ImageView>(R.id.tensorImg)
        cropImgRight = findViewById<ImageView>(R.id.cropImgRight)
        cropImgLeft = findViewById<ImageView>(R.id.cropImgLeft)

        init()
    }
    private fun init() {
        var activityIntent = intent
        val testUri = activityIntent.getStringExtra("uri")
        val parsedUri = Uri.parse(testUri)
        path = absolutelyPath(parsedUri)
        Log.d("path",path)
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        val src = BitmapFactory.decodeFile(path, options)
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        resized = Bitmap.createScaledBitmap(src, width/2, height/2, true)

        if (testUri != null) {
            Glide.with(this)
                .load(parsedUri)
                .into(img)
            getEyeContour(parsedUri)
        }
    }

    private fun extractColor(imageBitmap: Bitmap) : String{ // bitmap으로 이미지 전달받아서 색상 추출
        var bitmap = imageBitmap
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // ARGB_8888 bitmaps 만 support하는 오류 잡기 위한 코드

        val palette = Palette.from(bitmap).generate()
        val swatches = palette.swatches
        var maxPop = 0
        var maxHex : String = "#ffffff"
        for (swatch in swatches) {
            if (swatch != null && swatch.population > 0) {
                val pop = swatch.population
                val color = swatch.rgb
                val red = color shr 16 and 0xFF
                val green = color shr 8 and 0xFF
                val blue = color and 0xFF
                val rgb = red shl 16 or (green shl 8) or blue
                val hex = String.format("#%06X", rgb and 0xFFFFFF)
                if(maxPop<pop) {
                    maxPop = pop
                    maxHex = hex
                }
            }
        }
        return maxHex
    }
    private fun getEyeContour(imageUri: Uri){
        try {
            val image: InputImage = InputImage.fromFilePath(this, imageUri)
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) // fast / accurate로 나뉨
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(options)

            detector.process(image)
                .addOnSuccessListener { faces ->
                    for (face in faces){
                        val leftEyeContour = face.allContours[5]
                        val rightEyeContour = face.allContours[6]

                        var maxLeftX : Float = 0.0F
                        var minLeftX : Float = 9999.0F
                        var maxLeftY : Float = 0.0F
                        var minLeftY : Float = 9999.0F
                        var maxRightX : Float = 0.0F
                        var minRightX : Float = 9999.0F
                        var maxRightY : Float = 0.0F
                        var minRightY : Float = 9999.0F

                        for (idx in leftEyeContour.points){
                            if(maxLeftX<idx.x)
                                maxLeftX = idx.x
                            if(minLeftX>idx.x)
                                minLeftX = idx.x
                            if (maxLeftY<idx.y)
                                maxLeftY = idx.y
                            if(minLeftY>idx.y)
                                minLeftY = idx.y
                        }
                        for (idx in rightEyeContour.points){
                            if(maxRightX<idx.x)
                                maxRightX = idx.x
                            if(minRightX>idx.x)
                                minRightX = idx.x
                            if (maxRightY<idx.y)
                                maxRightY = idx.y
                            if(minRightY>idx.y)
                                minRightY = idx.y
                        }
                        val filePath = absolutelyPath(imageUri)
                        val rotatedResizedBitmap : Bitmap = rotatedBitmap(resized,filePath)!!

                        val leftResult = cropImage(minLeftX,minLeftY,maxLeftX,maxLeftY,imageUri,cropImgLeft,"leftEye")
                        val rightResult = cropImage(minRightX,minRightY,maxRightX,maxRightY,imageUri,cropImgRight,"rightEye")
                        if(leftResult!=null && rightResult !=null){
                            val list : List<String> = arrayListOf(leftResult,rightResult)
                            insert(rotatedResizedBitmap,path,list)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    private fun cropImage(
        minX: Float,
        minY: Float,
        maxX: Float,
        maxY: Float,
        imageUri: Uri,
        img: ImageView,
        name : String
    ): String {
        val filePath = absolutelyPath(imageUri)

        val originalImage =
            MediaStore.Images.Media.getBitmap(contentResolver, imageUri) // 카메라로 찍을 때 회전되어 나타남.

        val rotatedBitmap : Bitmap = rotatedBitmap(originalImage,filePath)!!
        Log.d("cropImage", "$minX  $minY  $maxX  $maxY")
        val x = minX.toInt() // The x coordinate of the top-left corner of the crop area

        val y = minY.toInt() // The y coordinate of the top-left corner of the crop area

        val width = (maxX - minX).toInt() // The width of the crop area

        val height = (maxY - minY).toInt() // The height of the crop area
        Log.d("croppedImage","$x $y $width $height")

        val croppedImage = Bitmap.createBitmap(rotatedBitmap, x, y, width, height)
        img.setImageBitmap(croppedImage)
        val uri = bitmapToRealPath(croppedImage,name)
        filePathList.add(uri)
        return extractColor(croppedImage)
    }

    private fun insert(img: Bitmap, path: String, infoList : List<String>){
        val file = File(path)
        var out :OutputStream ?= null
        try{
            file.createNewFile()
            out = FileOutputStream(file)
            img.compress(Bitmap.CompressFormat.JPEG,50,out) // 용량 확인해보기
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }finally {
            try{
                out?.close()
            }catch (e : IOException){
                e.printStackTrace()
            }
        }
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        Log.d("file",file.name)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val user = "catester"
        val info = infoList.toString()

        // 여러 file들을 담아줄 ArrayList
        val files: ArrayList<MultipartBody.Part> = ArrayList()
        files.add(body)

        // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...

        for (i in 0 until filePathList.size) {
            // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
            val fileBody: RequestBody =
                RequestBody.create(MediaType.parse("image/jpeg"), filePathList[i])

            val fileName = "photo$i.jpg"

            val filePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", fileName, fileBody)

            files.add(filePart)
        }
        client.insertImg(files, user, info).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    Toast.makeText(this@TensorCameraActivity, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@TensorCameraActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                    Log.d("response",response.isSuccessful.toString())
                    Log.d("response",response.body().toString())
                    Log.d("response",response.message())
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })
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

    private fun rotatedBitmap(bitmap: Bitmap?,filepath: String): Bitmap? {
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
    private fun absolutelyPath(path: Uri?): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)
        Log.d("result",result.toString())
        return result!!
    } // 절대경로로 변환하는 함수
    fun bitmapToRealPath(bitmap: Bitmap, name: String): String {
        val file = File(cacheDir, name)
        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }
}