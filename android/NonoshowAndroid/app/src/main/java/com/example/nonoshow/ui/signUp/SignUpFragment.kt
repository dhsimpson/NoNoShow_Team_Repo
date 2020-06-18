package com.example.nonoshow.ui.signUp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_TEXT
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.nonoshow.MyApplication
import com.example.nonoshow.MyApplication.Companion.managerMode
import com.example.nonoshow.MyApplication.Companion.trySignUp
import com.example.nonoshow.MyApplication.Companion.trySignUpManager
import com.example.nonoshow.R
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() { /*회원가입*/

    private lateinit var toolsViewModel: SignUpViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.isLogined = false
        val signUp : Button = requireView().findViewById(R.id.button_signup)
        signUp.setOnClickListener{
            MyApplication.isLogined = false
            val id = nickName.text.toString()
            val name = Name.text.toString()
            val pw = password.text.toString()
            val ageOrAddress = age.text.toString()
            val phoneNumber = phoneNum.text.toString()
            if(checkInput(id = id, name = name,
                    pw = pw, ageOrAddress = ageOrAddress,
                    phoneNumber = phoneNumber)) {
                signUpRequest(
                    id = id, name = name,
                    pw = pw, ageOrAddress = ageOrAddress,
                    phoneNumber = phoneNumber,
                    isManager = managerMode
                )
            }
            else{
                Log.d("err","input not found")
            }
            it.findNavController().navigate(R.id.nav_signIn)
        }

        uiChange(managerMode)
        switch_manager_join.isChecked = managerMode
        var isManager = false
        switch_manager_join.setOnClickListener{
            isManager = switch_manager_join.isChecked
            managerMode = isManager /*set global value*/
            uiChange(isManager)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        toolsViewModel =
            ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_sign_up, container, false)
        return root
    }

    @SuppressLint("SetTextI18n")
    private fun uiChange(isManager : Boolean = false){
/*visible setting*/
        if(isManager) {
            button_signup.text = "관리자 회원가입"
            switch_manager_join.text = "관리자모드 ON"
            Name.hint = "회사 이름"
            age.hint = "주소"
            age.inputType = TYPE_CLASS_TEXT
            title_SignUP.text = "관리자 회원가입"
        }
        else{
            button_signup.text = "회원가입"
            switch_manager_join.text = "관리자모드 OFF"
            Name.hint = "이름"
            age.hint = "나이"
            age.inputType = TYPE_CLASS_NUMBER
            title_SignUP.text = "회원가입"
        }
    }

    /*회원가입요청*/
    private fun signUpRequest(phoneNumber : String, name : String,
                              id : String, pw : String,
                              ageOrAddress : String, isManager : Boolean){
        when(isManager){
            true->{
                Log.i("ageOrAddress.toInt()",""+ageOrAddress)
                Log.i("name",name)
                Log.i("id",id)
                Log.i("pw",pw)
                Log.i("phone",phoneNumber)
                Thread{trySignUpManager(phoneNumber,name,id,ageOrAddress,pw)}.start()
            }
            false->{
                Log.i("ageOrAddress.toInt()",""+ageOrAddress)
                Log.i("name",name)
                Log.i("id",id)
                Log.i("pw",pw)
                Log.i("phone",phoneNumber)
                Thread{trySignUp(phoneNumber,name,id,ageOrAddress,pw)}.start()
                //Thread{custSignUp(name, id, pw, ageOrAddress.toInt(),phoneNumber)}.start()
            }
        }
    }

    private fun checkInput(phoneNumber : String = "", name : String = "",
                           id : String = "", pw : String = "",
                           ageOrAddress : String = ""): Boolean {
        return (phoneNumber != "") && (name != "") && (id != "")&& (pw != "") && (ageOrAddress != "")
    }
}