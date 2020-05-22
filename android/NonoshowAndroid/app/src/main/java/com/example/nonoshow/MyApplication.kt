package com.example.nonoshow

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.nonoshow.ui.company.CompanyManageFragment
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.prolificinteractive.materialcalendarview.*
import java.io.ByteArrayOutputStream
import java.util.*
import android.widget.Toast
import android.graphics.BitmapFactory
import com.example.nonoshow.ui.bookingMain.BookingMainFragment.Companion.DBListenerClient
import com.example.nonoshow.ui.company.CompanyManageFragment.Companion.createABlock
import java.io.File
import java.io.IOException


class MyApplication : Application() { /*하나의 인스턴스를 가지는 클래스*/
    companion object {
        var mDBReference : DatabaseReference?  = null
        var childUpdates : HashMap<String, Object>?  = null
        var userValue : Map<String, Object>?  = null
        val mDatabace : DatabaseReference = FirebaseDatabase.getInstance().reference
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        var managerInfo : ManagerInfo? = null
        const val LINEAR_LAYOUT = 1004
        const val TEXT_VIEW = 1015
        const val IMAGE_BUTTON = 1026
        const val LINE = 1037
        const val CALENDAR = 1048
        const val SPINNER = 1059
        const val DEFAULT = 8000
        const val LOGINED = 0
        @SuppressLint("StaticFieldLeak")
        var contextForList: Context? = null
        var state = DEFAULT /*내 상태 저장*/
        fun logout() {
            ID = "default"
            PW = "default"
            isLogined = false
            var loginToken = ""
        }

        /*static at Kotlin*/
        var ID = "default"
        var PW = "default"
        var isLogined = false
        var loginToken = "" /*서버에서 암호화해서 보내준 녀석을 저장<나중에 업데이트>*/
        var bookingTextView : TextView? = null

        var managerMode : Boolean = false

        fun <T> createView(
            type: Int,     /*0 = LL 1 = textView 2 = ImageButton */
            directionHorizontal: Boolean = false,    /*레이아웃 방향 true = 가로*/
            text: String = "",
            textSize: Float = 24f,
            width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            marginHorizontal: Int = 0, /*가로 마진*/
            marginVertical: Int = 0,
            marginLeft: Int = marginHorizontal,
            marginTop: Int = marginVertical,
            marginRight: Int = marginHorizontal,
            marginBottom: Int = marginVertical,
            backGroundColor: Int = R.color.colorWhite,
            weight: Float = 0f,
            imageId: Int = R.color.colorWhite,
            background: Int = backGroundColor,
            textColor: Int = android.R.color.black,
            textAlignCenter : Boolean = false,
            startNum : Int = 0,
            endNum : Int = 24,
            list: Array<String> = Array(endNum-startNum+1/*배열크기*/){""}.apply{   /*list를 직접 넣지않을경우 숫자로만 이루어진 아이템들을 설정*/
                var count = 0
                for (i in startNum..endNum) {
                    this[count++] = (i).toString()
                }
            }

        ): T? {
            val context = contextForList!! /*context 문제*/
            var result: T = View(context) as T
            when (type) {
                LINEAR_LAYOUT -> {   /*LL*/
                    result = LinearLayout(context).apply {
                        when (directionHorizontal) {
                            true -> orientation = LinearLayout.HORIZONTAL
                            false -> orientation = LinearLayout.VERTICAL
                        }
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        val param = layoutParams as ViewGroup.MarginLayoutParams    /*마진설정*/
                        param.setMargins(marginLeft, marginTop, marginRight, marginBottom)
                        setBackgroundColor(     /*배경 색 설정*/
                            ContextCompat.getColor(
                                context,
                                backGroundColor
                            )
                        )  /*backgroundColor 설정*/
                        weightSum = 1f
                    } as T
                }
                TEXT_VIEW -> {  /*textView*/
                    result = TextView(context).apply {
                        this.text = text /*  이곳에 매장의 이름이 들어와야 함  */
                        this.textSize = textSize
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                textColor
                            )
                        )
                        if(textAlignCenter){
                            textAlignment = View.TEXT_ALIGNMENT_CENTER
                            gravity = Gravity.CENTER
                        }
                        val param = layoutParams as ViewGroup.MarginLayoutParams    /*마진설정*/
                        param.setMargins(marginLeft, marginTop, marginRight, marginBottom)
                        this.background = ContextCompat.getDrawable(context, background)
                    } as T
                }
                IMAGE_BUTTON -> { /*ImageButton*/
                    result = ImageButton(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        this.setImageResource(imageId)    /*사진도 나중에 구현*/
                        this.background = ContextCompat.getDrawable(context, background)
                        adjustViewBounds = true
                    } as T
                }
                LINE -> {   /*line*/
                    result = View(context).apply {
                        when (directionHorizontal) {    /*true 면 가로선 (height 가 6)*/
                            true -> layoutParams =
                                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 6)
                            false -> layoutParams =
                                LinearLayout.LayoutParams(6, ViewGroup.LayoutParams.MATCH_PARENT)
                        }
                        setBackgroundColor(     /*배경 색 설정*/
                            ContextCompat.getColor(
                                context,
                                backGroundColor
                            )
                        )
                    } as T
                }
                CALENDAR -> {
                    result = MaterialCalendarView(context).apply {
                        state().edit()
                            .setFirstDayOfWeek(Calendar.SUNDAY)
                            .setMinimumDate(CalendarDay.from(2017, 0, 1))
                            .setMaximumDate(CalendarDay.from(2030, 11, 31))
                            .setCalendarDisplayMode(CalendarMode.MONTHS)
                            .commit()
                        setOnDateChangedListener { widget, date, selected ->
                            run{
                                Log.i("calendar", date.toString())
                            }
                        }  /*날짜가 선택됐을 때*/
                    } as T
                }
                SPINNER -> {
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter(context, android.R.layout.simple_dropdown_item_1line, list)
                    result = Spinner(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            width,
                            height
                        ).apply {
                            if (weight != 0f)
                                this.weight = weight
                        }
                        ListView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                width,
                                height
                            )
                        }
                        setAdapter(adapter)
                    } as T
                }
            }
            return result
        }

        /******
         *이더리움 함수 이름 custSignIn
         *******/
        fun trySignIn(id : String ="",pw : String="",it : View?) : String{ /*이더리움으로 부터 "client"->상태 고객 고유 ID와 true값을 받아 고유ID를 반환함*/
            FirebaseDatabase.getInstance().reference.child("User_info").addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.e("trySignIn","key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1)
                    if(id == dataSnapshot.key){
                        if(dataSnapshot.child("pw").value == pw){
                            Log.i("Login : ", "welcome $id")
                            MainActivity.changeState(ID, LOGINED)/*로그인 성공시 상태를 변경하며, 닉네임설정*/
                            isLogined = true
                            state = LOGINED
                            it!!.findNavController().navigate(R.id.nav_booking)    /*fragment 전환*/
                        }
                        else{
                            Log.i("Login : ", "wrong password")
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

            var result = "err"

            return result
        }


        fun trySignInManager(id : String ="",pw : String="",it : View?) : String{ /*이더리움으로 부터 "client"->상태 고객 고유 ID와 true값을 받아 고유ID를 반환함*/
            FirebaseDatabase.getInstance().reference.child("Manager_info").addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.e("trySignInManager","key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1)
                    if(id == dataSnapshot.key){
                        if(dataSnapshot.child("pw").value == pw){
                            Log.i("ManagerLogin : ", "welcome $id")
                            MainActivity.changeState(ID, LOGINED)/*로그인 성공시 상태를 변경하며, 닉네임설정*/
                            state = LOGINED
                            isLogined = true
                            managerInfo = ManagerInfo(id, pw, dataSnapshot.child("name").value.toString(), dataSnapshot.child("address").value.toString(),
                                dataSnapshot.child("phoneNum").value.toString())
                            it!!.findNavController().navigate(R.id.nav_booking)    /*fragment 전환*/
                        }
                        else{
                            Log.i("ManagerLogin : ", "wrong password")
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })

            var result = "err"

            return result
        }
        val arrayList : ArrayList<CompanyInfo> = ArrayList()
        fun tryLookComp(name : String? = null,isManager : Boolean,id : String? = null) : ArrayList<CompanyInfo>{
            FirebaseDatabase.getInstance().reference.child("Company_info").addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.e("Company_info","key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1)
                    var companyInfo : CompanyInfo? = null
                    when (name) {
                        null -> companyInfo=CompanyInfo(
                            dataSnapshot.child("id").value.toString(),
                            dataSnapshot.child("name").value.toString(),
                            dataSnapshot.child("address").value.toString(),
                            dataSnapshot.child("phoneNumber").value.toString(),
                            dataSnapshot.child("imageSrc").value.toString(),
                            dataSnapshot.child("info").value.toString()
                        )
                        dataSnapshot.key -> companyInfo=CompanyInfo(
                            dataSnapshot.child("id").value.toString(),
                            dataSnapshot.child("name").value.toString(),
                            dataSnapshot.child("address").value.toString(),
                            dataSnapshot.child("phoneNumber").value.toString(),
                            dataSnapshot.child("imageSrc").value.toString(),
                            dataSnapshot.child("info").value.toString()
                        )
                        else -> {
                            if(ID == dataSnapshot.child("id").value.toString())
                            { companyInfo=CompanyInfo(
                                dataSnapshot.child("id").value.toString(),
                                dataSnapshot.child("name").value.toString(),
                                dataSnapshot.child("address").value.toString(),
                                dataSnapshot.child("phoneNumber").value.toString(),
                                dataSnapshot.child("imageSrc").value.toString(),
                                dataSnapshot.child("info").value.toString())}
                        }
                    }
                    if(companyInfo != null){
                        when(isManager){
                            false->{ DBListenerClient(companyInfo = companyInfo)  }
                            true->{ CompanyManageFragment.DBlistener(companyInfo) }
                        }
                    }

                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    Log.i("listen","child changed")
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
            Thread.sleep(800)
            val result = arrayList
            arrayList.clear()
            return result
        }

        fun trySignUp(phoneNumber : String, name : String, id : String, age : String, pw : String) : Boolean {    /*회원가입*/

            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()

            val userInfo = UserInfo(id, pw, name, age, phoneNumber)
            userValue = userInfo.toMap() as Map<String, Object>?

            childUpdates!!["/User_info/" + id] = userValue as Object
            mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            return false
        }

        fun trySignUpManager(phoneNumber : String, name : String, id : String, address : String, pw : String) : Boolean {    /*회원가입 manager*/

            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()

            val managerInfo = ManagerInfo(id, pw, name, address, phoneNumber)
            userValue = managerInfo.toMap() as Map<String, Object>?

            childUpdates!!["/Manager_info/" + id] = userValue as Object
            mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            return false
        }

        fun trySaveComp(phoneNumber : String, name : String, id : String, address : String, info : String, view : ImageView) : Boolean {
            uploadImage("$name$id.jpeg",view)
            /*회원가입 manager*/
            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()

            val companyInfo = CompanyInfo(id, name, address,
                phoneNumber, "$name$id.jpeg",info)
            userValue = companyInfo.toMap() as Map<String, Object>?

            childUpdates!!["/Company_info/" + name] = userValue as Object
            mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            return false
        }

        fun getImage(imageSrc : String,imageView : ImageView){
            val src = "images/$imageSrc"
            Log.i("src",src)
            val imageRef = storageReference.child(src)
            try {
                // Storage 에서 다운받아 저장시킬 임시파일
                val imageFile = File.createTempFile("images", "jpg")
                imageRef.getFile(imageFile)
                    .addOnSuccessListener {
                        // Success Case
                        val bitmapImage = BitmapFactory.decodeFile(imageFile.path)
                        imageView.setImageBitmap(bitmapImage)
                    }.addOnFailureListener { e ->
                        // Fail Case
                        e.printStackTrace()
                        Toast.makeText(contextForList, "이미지 가져오기 실패", Toast.LENGTH_LONG).show()
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun uploadImage(imageName : String, path : ImageView){
            val storageRef = storageReference.child("images/$imageName" )
            path.isDrawingCacheEnabled = true
            path.buildDrawingCache()
            val bitmap = (path.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = storageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
            }.addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
            }
        }
    }
}