package com.example.nonoshow

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.nonoshow.AWSService.Companion.uploadImageS3
import com.example.nonoshow.MyApplication.Companion.managerInfo
import com.example.nonoshow.MyApplication.Companion.trySaveComp
import kotlinx.android.synthetic.main.activity_modify_comp.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class modify_comp : AppCompatActivity() {
    internal val REQUEST_IMAGE_CAPTURE = 500
    var bitmap : Bitmap? = null
    var filePath : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_comp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("권한", " 설정 완료")
            } else {
                Log.d("권한", " 설정 요청")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
        if(managerInfo != null){
            initSetting(managerInfo!!)
            confirmButton.setOnClickListener{
                Log.i("filePath",filePath!!)
                confirm(managerInfo!!,filePath!!)
            }
        }
        cancelButton.setOnClickListener{
            cancel()
        }
    }

    private fun initSetting(managerInfo : ManagerInfo){
        compName.setText(managerInfo.name)
        compAddress.setText(managerInfo.address)
        TextViewSelectImage.text = "터치해서 사진선택"
        imageButton.setOnClickListener{
            getPicture()
        }
    }

    private fun cancel(){
        this.finish()
    }

    private fun confirm(managerInfo : ManagerInfo,filePath : String){
        trySaveComp(managerInfo.phoneNum, compName.text.toString(), managerInfo.id , compAddress.text.toString(), compInfo.text.toString(), imageButton)
        //uploadImageS3(filePath)
        this.finish()
    }

    fun getPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val image = imageButton
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
        }
        super.onActivityResult(requestCode, resultCode, data)
        val currentUri =getPath(data!!.data!!)
        filePath = currentUri
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    val `in` = contentResolver.openInputStream(data!!.data!!)
                    val img = BitmapFactory.decodeStream(`in`)
                    `in`!!.close()
                    // 이미지뷰에 세팅
                    image.setImageBitmap(img)
                    Log.d("imgBitmap",img.toString())
                    filePath
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
    private fun getPath(uri : Uri) : String {
        val projection : Array<String>  = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        startManagingCursor(cursor)
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }


}
