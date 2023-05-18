package com.example.healthapp.mysql.api

import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ImgApi {

    @Multipart
    @POST("image/upload")
    fun insertImg(
        @Part files: List<MultipartBody.Part>, // 이미지 파일을 전송할 것이기 때문에 데이터타입은 MultipartBody.Part 로 지정해야 합니다.
        @Part("user") user : String,
        @Part("info") info : String
    ): Call<String>

    @GET("image/download/{user}")
    @Streaming
    fun getImg(
        @Path("user") user : String
    ) //용량이 적을 경우 @Streaming은 생략이 가능하다.
     :Call<ResponseBody>
}