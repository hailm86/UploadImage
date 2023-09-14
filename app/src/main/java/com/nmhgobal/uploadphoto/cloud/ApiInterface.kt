package com.nmhgobal.uploadphoto.cloud

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {
    @POST("predict")
    fun uploadImages(@Body request: ImageRequest): Call<ResponseBody>

//    @Multipart
//    @POST("predict")
//    fun uploadImages222(
//        @Part image1: MultipartBody.Part,
//        @Part image2: MultipartBody.Part
//    ): Call<ApiResponse>
}
