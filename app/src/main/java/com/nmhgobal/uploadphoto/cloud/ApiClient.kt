package com.nmhgobal.uploadphoto.cloud

import android.content.Context
import android.os.Environment
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class ApiClient(private val context: Context) {

    private val apiBaseUrl = "http://45.76.185.103:8000/remove-object/api/" // Thay thế bằng đường dẫn API của bạn
    private val apiInterface: ApiInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiInterface = retrofit.create(ApiInterface::class.java)
    }

//    fun uploadImages(imageFile1: File, imageFile2: File) {
//        val requestFile1 = imageFile1.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val requestFile2 = imageFile2.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//
//        val imagePart1 = MultipartBody.Part.createFormData("image1", imageFile1.name, requestFile1)
//        val imagePart2 = MultipartBody.Part.createFormData("image2", imageFile2.name, requestFile2)
//
//        val call = apiInterface.uploadImages(imagePart1, imagePart2)
//
//        call.enqueue(object : Callback<ApiResponse> {
//            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
//                if (response.isSuccessful) {
//                    val message = response.body()?.message ?: "Upload successful"
//                    // Xử lý phản hồi từ API ở đây
//                } else {
//                    // Xử lý lỗi
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                // Xử lý lỗi
//            }
//        })
//    }
}
