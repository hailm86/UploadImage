package com.nmhgobal.uploadphoto

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.GsonBuilder
import com.nmhgobal.uploadphoto.cloud.ApiInterface
import com.nmhgobal.uploadphoto.cloud.ApiResponse
import com.nmhgobal.uploadphoto.cloud.ImageRequest
import com.nmhgobal.uploadphoto.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    private var bitmap1: Bitmap? = null
    private var bitmap2: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initialize()
    }

    private fun initialize() {
        mainBinding.imgOne.setOnClickListener {
            checkPer(REQUEST_CODE_1)
        }

        mainBinding.imgTwo.setOnClickListener {
            checkPer(REQUEST_CODE_2)
        }

        mainBinding.btnSend.setOnClickListener {
            if (bitmap1 != null && bitmap2 != null) {
                val base64Image1 = bitmapToBase64(bitmap1!!)
                val base64Image2 = bitmapToBase64(bitmap2!!)

                println("base64Image1 => $base64Image1")
                println("base64Image2 => $base64Image2")
                sendImagesToApi(base64Image1, base64Image2)
            } else {
                Toast.makeText(applicationContext, "Please select 2 image", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sendImagesToApi(image1: String, image2: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://45.76.185.103:8000/remove-object/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)

        // Tạo đối tượng RequestData với image và mask
        val requestData = ImageRequest(image1, image2)

        // Gọi phương thức POST để gửi dữ liệu
        val call = apiInterface.uploadImages(requestData)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
//                    val message = response.body()?.message ?: "Request successful"
                    // Xử lý phản hồi từ API ở đây
                    Toast.makeText(applicationContext, "Upload successful", Toast.LENGTH_SHORT)
                        .show()
                    val bitmap = BitmapFactory.decodeStream(
                        (response.body()?.byteStream() ?: bitmap1) as InputStream?
                    )
                    mainBinding.imgResult.setImageBitmap(bitmap)
                } else {
                    // Xử lý lỗi
                    Toast.makeText(applicationContext, "Error => Upload fail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Xử lý lỗi
                println("Error => $t")
                Toast.makeText(applicationContext, "Upload fail + $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    private fun checkPer(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Nếu quyền chưa được cấp, yêu cầu quyền
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE),
                requestCode
            )
        } else {
            openGallery(requestCode)
        }
    }

    private fun openGallery(requestCode: Int) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            try {
                val bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                if (requestCode == REQUEST_CODE_1) {
                    mainBinding.imgOne.setImageBitmap(bitmap)
                    bitmap1 = bitmap // Gán bitmap1
                } else if (requestCode == REQUEST_CODE_2) {
                    mainBinding.imgTwo.setImageBitmap(bitmap)
                    bitmap2 = bitmap // Gán bitmap2
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(REQUEST_CODE_1)
            } else {
                // Người dùng từ chối quyền, bạn có thể hiển thị thông báo hoặc thực hiện xử lý phù hợp
            }
        }
        if (requestCode == REQUEST_CODE_2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery(REQUEST_CODE_2)
            } else {
                // Người dùng từ chối quyền, bạn có thể hiển thị thông báo hoặc thực hiện xử lý phù hợp
            }
        }

    }


    companion object {
        const val REQUEST_CODE_1 = 1
        const val REQUEST_CODE_2 = 2
    }
}