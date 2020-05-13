package com.example.nonoshow.ui.signUp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.nonoshow.MyApplication
import com.example.nonoshow.MyApplication.Companion.managerMode
import com.example.nonoshow.R
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : Fragment() { /*회원가입*/

    private lateinit var toolsViewModel: SignUpViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MyApplication.isLogined = false
        val signUp : Button = getView()!!.findViewById(R.id.button_signup)

        signUp.setOnClickListener{
            MyApplication.isLogined = false
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
            title_SignUP.text = "관리자 회원가입"
        }
        else{
            button_signup.text = "회원가입"
            switch_manager_join.text = "관리자모드 OFF"
            Name.hint = "이름"
            age.hint = "나이"
            title_SignUP.text = "회원가입"
        }
    }
}