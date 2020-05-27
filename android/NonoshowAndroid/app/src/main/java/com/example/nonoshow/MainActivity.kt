package com.example.nonoshow

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import com.example.nonoshow.EthereumService.*
import com.example.nonoshow.MyApplication.Companion.contextForList
import com.example.nonoshow.MyApplication.Companion.isLogined
import com.example.nonoshow.MyApplication.Companion.managerMode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.Signature


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
Log.i("set","created")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        isLogined = false
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_search_by_phoneNum, R.id.nav_signIn, R.id.nav_booking, R.id.nav_booking_List,R.id.nav_company_manage
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        contextForList = this
        val webView : WebView = WebView(contextForList)
        webView.apply{loadUrl("textContract.func3()")}
        getHashKey()
        Thread {
            //getCredential()
            //getBalance()
            //custSignUp("test","11","22",88888888,"01015156464")   /*이더리움 연결*/
            //custLogIn("blackcow","12345678")
        }.start()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        nickname = findViewById(R.id.menuNickNameText)
        signOutText = findViewById(R.id.signOutText)
        signOutText!!.visibility = View.INVISIBLE
        signOutText!!.setOnClickListener{
            changeState("logout",LOGOUT)
        }
        return true
    }

    override fun onResume(){
        super.onResume()
Log.i("set","resume~")
    }

    override fun onRestart(){
        super.onRestart()
Log.i("set","restart!")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        nickname!!.setOnClickListener{
            if(!isLogined)
                navController.navigate(R.id.nav_signIn)
        }
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    companion object {
        private const val LOGIN: Int = 0
        const val LOGOUT: Int = 1
        @SuppressLint("StaticFieldLeak")
        var nickname: TextView? = null
        @SuppressLint("StaticFieldLeak")
        var signOutText: TextView? = null

        fun changeState(data: String, index: Int) {
            when (index) {
                LOGIN -> {
                    nickname!!.text = data
                    signOutText!!.visibility = View.VISIBLE
                }
                LOGOUT -> {
                    MyApplication.logout()
                    nickname!!.text = "로그인 해 주세요"
                    signOutText!!.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun getHashKey() {  /*only for get HashKey*/
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null)
            Log.e("HashKey", "GashKey:null")
        for (  signature in packageInfo!!.signatures){
            try{
                val md : MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray());
                Log.d("HashKey",Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }catch(e : NoSuchAlgorithmException){
                Log.e("HashKey", "HashKey Error. signature=$signature", e)
            }
        }

    }


}
