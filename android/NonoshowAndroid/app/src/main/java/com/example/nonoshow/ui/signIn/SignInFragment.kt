package com.example.nonoshow.ui.signIn

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.nonoshow.EthereumService.custLogIn
import com.example.nonoshow.MyApplication
import com.example.nonoshow.MyApplication.Companion.managerMode
import com.example.nonoshow.MyApplication.Companion.trySignIn
import com.example.nonoshow.R
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {

    private lateinit var signInViewModel: SignInViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        MyApplication.isLogined = false
        val logoButton : ImageView = requireView().findViewById(R.id.logoImage)
        val signIn : Button = requireView().findViewById(R.id.button_signIn)
        val signUp : Button = requireView().findViewById(R.id.button_signUp)
        val checkImage : ImageView = requireView().findViewById(R.id.checkImage)
        val textID : EditText = requireView().findViewById(R.id.editText_ID)
        val textPW : EditText = requireView().findViewById(R.id.editText_PW)
        logoButton.setOnClickListener{
            val uri = Uri.parse("http://github.com/haebeompark/NonoshowAndroid")
            val intent : Intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        }
        var isChecked = false
        checkImage.setOnClickListener{ /*로그인 상태 유지가 클릭됐을 때*/
            when(isChecked){
                true-> {
                    checkImage.setImageResource(R.drawable.check_gray)
                    isChecked = false
                }
                false->{
                    checkImage.setImageResource(R.drawable.check_colored)
                    isChecked = true
                }
            }
        }
        uiChange(managerMode)
        switch_manager.isChecked = managerMode
        var isManager = false
        switch_manager.setOnClickListener{
            isManager = switch_manager.isChecked
            managerMode = isManager /*set global value*/
            uiChange(isManager)
        }

        /*signIn 버튼 클릭시 MyApplication클래스의 trySignIn함수를 불러오게 되고 이더리움 통신을 위한 데이터 또는  토큰을 받아온다*/
        signIn.setOnClickListener{
            /*Thread {
                custLogIn(
                    editText_ID.text.toString(),
                    editText_PW.text.toString()
                )/* 이더리움서비스클래스메서드호출 */
            }.start()*/
            MyApplication.isLogined = true
            MyApplication.ID = textID.text.toString()
Log.i("ID",MyApplication.ID)
            MyApplication.PW = textPW.text.toString()
Log.i("PW",MyApplication.PW)
            val id = editText_ID.text.toString()
            val pw = editText_PW.text.toString()

            trySignIn(id, pw,it)
        }
        signUp.setOnClickListener{
            MyApplication.isLogined = false
            it.findNavController().navigate(R.id.nav_signUp)
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInViewModel =
            ViewModelProviders.of(this).get(SignInViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_sign_in, container, false)

        return root
    }

    override fun onResume() {
        super.onResume()
        uiChange(managerMode)
        switch_manager.isChecked = managerMode
    }

    @SuppressLint("SetTextI18n")
    private fun uiChange(isManager : Boolean = false){  /*false : 고객로그인모드, true : 관리자로그인모드*/
        /*visible setting*/
        if(isManager) {
            switch_manager.text = "관리자모드 ON"
            button_signIn.text = "관리자 로그인"
        }
        else{
            switch_manager.text = "관리자모드 OFF"
            button_signIn.text = "로그인"
        }
    }
}