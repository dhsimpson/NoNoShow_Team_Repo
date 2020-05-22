package com.example.nonoshow

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nonoshow.MyApplication.Companion.managerInfo
import com.example.nonoshow.MyApplication.Companion.trySaveComp
import kotlinx.android.synthetic.main.activity_modify_comp.*

class modify_comp : AppCompatActivity() {
    internal val REQUEST_IMAGE_CAPTURE = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_comp)
        if(managerInfo != null){
            initSetting(managerInfo!!)
            confirmButton.setOnClickListener{
                confirm(managerInfo!!)
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

    private fun confirm(managerInfo : ManagerInfo){
        trySaveComp(managerInfo.phoneNum, compName.text.toString(), managerInfo.id , compAddress.text.toString(), compInfo.text.toString(), imageButton)
        this.finish()
    }

    fun getPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val image = imageButton
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
        }
        super.onActivityResult(requestCode, resultCode, data)

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

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}
