package com.example.healthapp.view

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

class TensorGalleryActivity : AppCompatActivity() {
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var img : ImageView
    private lateinit var text : TextView
    private lateinit var cropImgRight : ImageView
    private lateinit var cropImgLeft : ImageView
    private var filePathList = ArrayList<String>()
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
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = checkNotNull(result.data)
                val imageUri = intent.data // 갤러리에서 선택한 사진 받아옴
                Glide.with(this)
                    .load(imageUri)
                    .into(img)

                if (imageUri != null) { // 갤러리에서 사진 고를 때
                    tensor3(imageUri)
                }
            }
        }

        img.setOnClickListener {
            val intent = Intent().also { intent ->
                intent.type = "image/"
                intent.action = Intent.ACTION_GET_CONTENT
            }
            launcher.launch(intent)
        }
    }

    private fun tensor2(imageUri: Uri){ // 이미지 uri로 전달받아서 색상 추출
        var bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,imageUri)) // 선택한 사진 uri -> bitmap 변환
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true) // ARGB_8888 bitmaps 만 support하는 오류 잡기 위한 코드

        val palette = Palette.from(bitmap).generate()

        val swatches = palette.swatches
        for (swatch in swatches) {
            if (swatch != null && swatch.population > 0) {
                val pop = swatch.population
                val color = swatch.rgb
                val red = color shr 16 and 0xFF
                val green = color shr 8 and 0xFF
                val blue = color and 0xFF
                Log.d("red green blue","$red $green $blue")
                val rgb = red shl 16 or (green shl 8) or blue
                val hex = String.format("#%06X", rgb and 0xFFFFFF)
                Log.d("hex","$hex  $pop")
            }
        }

    }

    private fun tensor3(imageUri: Uri){
        try {
           val image: InputImage = InputImage.fromFilePath(this, imageUri)
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST) // fast / accurate로 나뉨
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(options)

            detector.process(image)
                .addOnSuccessListener { faces ->
//                    Log.d("faces",faces.toString())
                    for (face in faces){
                        val leftEyeContour = face.allContours[5]
                        val rightEyeContour = face.allContours[6]
                        val noseBottomContour = face.allContours[12]

                        var maxLeftX : Float = 0.0F
                        var minLeftX : Float = 9999.0F
                        var maxLeftY : Float = 0.0F
                        var minLeftY : Float = 9999.0F
                        var maxRightX : Float = 0.0F
                        var minRightX : Float = 9999.0F
                        var maxRightY : Float = 0.0F
                        var minRightY : Float = 9999.0F

                        var minNoseX : Float = 9999.0F
                        var maxNoseX : Float = 0.0F
                        var minNoseY : Float = 9999.0F
                        var maxNoseY : Float = 0.0F

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
                        for (idx in noseBottomContour.points){
                           if(minNoseX > idx.x)
                               minNoseX = idx.x
                            if(minNoseY > idx.y)
                                minNoseY = idx.y
                            if(maxNoseX < idx.x)
                                maxNoseX = idx.x
                            if(maxNoseY < idx.y)
                                maxNoseY = idx.y
                        }
                        val leftCheekCenterX = face.allContours[13].points[0].x
                        val leftCheekCenterY = face.allContours[13].points[0].y
                        val rightCheekCenterX = face.allContours[14].points[0].x
                        val rightCheekCenterY = face.allContours[14].points[0].y

                        val leftCheekWidth = (minNoseX- leftCheekCenterX)
                        val rightCheekWidth = (rightCheekCenterX - maxNoseX)


                        val leftResult = cropImage(minLeftX,minLeftY,maxLeftX,maxLeftY,imageUri,cropImgLeft,"leftEye")
                        val rightResult = cropImage(minRightX,minRightY,maxRightX,maxRightY,imageUri,cropImgRight,"rightEye")
//                        cropImageCenter(leftCheekCenterX,leftCheekCenterY, leftCheekWidth, imageUri, cropImgLeft)
//                        cropImageCenter(rightCheekCenterX,rightCheekCenterY,rightCheekWidth,imageUri,cropImgRight)
                        if(leftResult!=null&&rightResult!=null){
                            val list : List<String> = arrayListOf(leftResult,rightResult)
                            insert(imageUri,list)
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

    private fun tensor4(imageBitmap: Bitmap) : String{ // bitmap으로 이미지 전달받아서 색상 추출
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

    private fun cropImage(
        minX:Float,
        minY:Float,
        maxX:Float,
        maxY:Float,
        imageUri: Uri,
        img:ImageView,
        name : String
    ) : String{
        val originalImage =
            MediaStore.Images.Media.getBitmap(contentResolver,imageUri)

        val x = minX.toInt() // The x coordinate of the top-left corner of the crop area

        val y = minY.toInt() // The y coordinate of the top-left corner of the crop area

        val width = (maxX-minX).toInt() // The width of the crop area

        val height = (maxY-minY).toInt() // The height of the crop area

        val croppedImage = Bitmap.createBitmap(originalImage, x, y, width, height)

        img.setImageBitmap(croppedImage)

        val uri = bitmapToRealPath(croppedImage,name)
        filePathList.add(uri)

        return tensor4(croppedImage)
    } // 눈영역 잡을 때 crop
    private fun cropImageCenter(centerX:Float,centerY:Float,width:Float,imageUri: Uri,img:ImageView){
        val originalImage =
            MediaStore.Images.Media.getBitmap(contentResolver,imageUri)

        val x = (centerX-width).toInt() // The x coordinate of the top-left corner of the crop area

        val y = (centerY-width).toInt() // The y coordinate of the top-left corner of the crop area

        val width = (width*2).toInt() // The width of the crop area

        val croppedImage = Bitmap.createBitmap(originalImage, x, y, width, width)

        img.setImageBitmap(croppedImage)
        tensor4(croppedImage)
    } // cheek 영역 잡을 때 crop

    private fun insert(imageUri:Uri, infoList : List<String>){
        val file = File(absolutelyPath(imageUri)) // path 동일
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        Log.d("file",file.name)
        Log.d("path",file.absolutePath)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val user = "gatester"
        val info = infoList.toString()

        // 여러 file들을 담아줄 ArrayList
        val files: ArrayList<MultipartBody.Part> = ArrayList()
        files.add(body)

        // 파일 경로들을 가지고있는 `ArrayList<Uri> filePathList`가 있다고 칩시다...
        Log.d("filePathList  ",filePathList.toString())

        for (i in 0 until filePathList.size) {
            // Uri 타입의 파일경로를 가지는 RequestBody 객체 생성
            val fileBody: RequestBody =
                RequestBody.create(MediaType.parse("image/jpeg"), filePathList[i])

            val fileName = "photo$i.jpg"

            val filePart: MultipartBody.Part =
                MultipartBody.Part.createFormData("photo", fileName, fileBody)

            files.add(filePart)
            Log.d("files ",files.toString())
        }

        client.insertImg(files,user,info).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                    Toast.makeText(this@TensorGalleryActivity, "이미지 전송 성공", Toast.LENGTH_SHORT).show()
                }else{
                    Log.d("testt message",response.message())
                    Log.d("testt suc",response.errorBody().toString())
                    Log.d("testt succc",response.isSuccessful.toString())
                    Log.d("testt body",response.body().toString())
                    Log.d("testt",response.toString())
                    Toast.makeText(this@TensorGalleryActivity, "이미지 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })
    }
    fun absolutelyPath(path: Uri?): String {
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