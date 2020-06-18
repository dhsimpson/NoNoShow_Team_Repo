package com.example.nonoshow.ui.signIn

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.nonoshow.MyApplication
import com.example.nonoshow.MyApplication.Companion.DEFAULT
import com.example.nonoshow.MyApplication.Companion.folderName
import com.example.nonoshow.MyApplication.Companion.isLogined
import com.example.nonoshow.MyApplication.Companion.managerMode
import com.example.nonoshow.MyApplication.Companion.state
import com.example.nonoshow.MyApplication.Companion.trySignIn
import com.example.nonoshow.MyApplication.Companion.trySignInManager
import com.example.nonoshow.R
import com.example.nonoshow.data.translate
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

class SignInFragment : Fragment() {
    var isChecked = false
    private lateinit var signInViewModel: SignInViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        try {
            initSetting()
        }catch(e: FileNotFoundException){
            Log.i("file","not found")
        }
        isLogined = false

        val logoButton : ImageView = getView()!!.findViewById(R.id.logoImage)
        val signIn : Button = getView()!!.findViewById(R.id.button_signIn)
        val signUp : Button = getView()!!.findViewById(R.id.button_signUp)
        val checkImage : ImageView = getView()!!.findViewById(R.id.checkImage)
        val textID : EditText = getView()!!.findViewById(R.id.editText_ID)
        val textPW : EditText = getView()!!.findViewById(R.id.editText_PW)
        logoButton.setOnClickListener{
            val uri = Uri.parse("http://github.com/haebeompark/NonoshowAndroid")
            val intent : Intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        }
        checkImage.setOnClickListener{ /*로그인 상태 유지가 클릭됐을 때*/
            isChecked = when(isChecked){
                true-> {
                    checkImage.setImageResource(R.drawable.check_gray)
                    false
                }
                false->{
                    checkImage.setImageResource(R.drawable.check_colored)
                    true
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
            MyApplication.ID = textID.text.toString()
Log.i("ID",MyApplication.ID)
            MyApplication.PW = textPW.text.toString()
Log.i("PW",MyApplication.PW)
            val id = editText_ID.text.toString()
            val pw = editText_PW.text.toString()

            signIn(id, pw,it)
        }
        signUp.setOnClickListener{
            isLogined = false
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
        isLogined = false
        state = DEFAULT
    }

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
    private fun signIn(id : String, pw : String, it : View?){
        if(isChecked) { /*로그인 상태유지가 클릭된 경우*/
            val filePath = File("$folderName/data")
            val file = File("$folderName/data/forLogin.txt")
            val file_a = File("$folderName/data/forLoginAuto.txt")
            if (!filePath.exists()) {
                filePath.mkdir()
            }
            var saveStr = "${translate.twist(id)}&${translate.twist(pw)}"       /*암호화해서 저장*/
            saveStr = if (managerMode)
                "1&$saveStr"
            else
                "0&$saveStr"

            /*파일 쓰기*/
            file.bufferedWriter().use{ it.write(saveStr) }
            file_a.bufferedWriter().use{ it.write("1")}
        }
        else{
            val file_a = File("$folderName/data/forLoginAuto.txt")
            GlobalScope.launch(Dispatchers.IO) {
                /*파일 쓰기*/
                file_a.bufferedWriter().use{ it.write("0")}
            }
        }
        when(managerMode){
            true->{
                trySignInManager(id,pw,it)
            }
            false->{
                trySignIn(id,pw,it)
            }
        }
    }
    private fun initSetting(){
        val file = File("$folderName/data/forLogin.txt")
        val file_a = File("$folderName/data/forLoginAuto.txt")
        val autoLogin = file_a.readText()
        if(autoLogin == "1"){
            isChecked = true
            checkImage.setImageResource(R.drawable.check_colored)
            val forLogin = file.readText()
            val array = forLogin.split("&")
            if(array[0] =="1") {
                managerMode = true
            }
            editText_ID.setText(translate.solve(array[1]))  /*복호화*/
            editText_PW.setText(translate.solve(array[2]))
        }
    }

    fun File.readText() : String = TextUtils.join("\n", readLines())    /*읽기*/

}