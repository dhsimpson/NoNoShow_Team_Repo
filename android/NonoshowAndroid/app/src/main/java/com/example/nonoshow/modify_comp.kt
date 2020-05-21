package com.example.nonoshow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nonoshow.MyApplication.Companion.managerInfo
import com.example.nonoshow.MyApplication.Companion.trySaveComp
import kotlinx.android.synthetic.main.activity_modify_comp.*

class modify_comp : AppCompatActivity() {

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
        imageButton.text = "터치해서 사진선택"
    }

    private fun cancel(){
        this.finish()
    }

    private fun confirm(managerInfo : ManagerInfo,imageSrc : String = "noImage"){
        trySaveComp(managerInfo.phoneNum, compName.text.toString(), managerInfo.id , compAddress.text.toString(), compInfo.text.toString(), imageSrc)
        this.finish()
    }
}
