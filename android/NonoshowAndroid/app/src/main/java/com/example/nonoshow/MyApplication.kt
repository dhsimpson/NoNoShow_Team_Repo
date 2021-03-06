package com.example.nonoshow

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
import com.example.nonoshow.data.FcmPush
import com.example.nonoshow.ui.bookingList.BookingListFragment
import com.example.nonoshow.ui.bookingMain.BookingMainFragment
import com.example.nonoshow.ui.noShowManager.NoShowManagerFragment
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.IOException


class MyApplication : Application() { /*하나의 인스턴스를 가지는 클래스*/
    companion object {
        var ec2Address : String? = null
        var folderName : String? = null
        var mDBReference : DatabaseReference?  = null
        var childUpdates : HashMap<String, Object>?  = null
        var userValue : Map<String, Object>?  = null
        val mDatabace : DatabaseReference = FirebaseDatabase.getInstance().reference
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.reference
        var managerInfo : ManagerInfo? = null
        var userPhoneNum : String = "default"
        var reservationCompName : String? = null
        var ampm : String = "am"
        var hour : Int = 1
        var minute : Int = 0
        var numberOfPerson : Int = 1
        var userName : String = "UNKNOWN"
        var keyID : String = ""
        const val LINEAR_LAYOUT = 1004
        const val TEXT_VIEW = 1015
        const val IMAGE_BUTTON = 1026
        const val LINE = 1037
        const val CALENDAR = 1048
        const val SPINNER = 1059
        const val MAPVIEW = 1077
        const val DEFAULT = 8000
        const val LOGINED = 0
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
        var filePath = ""
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
            },
            spinnerType : String = "default"

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
                        this.setImageResource(imageId)
                        this.scaleType = ImageView.ScaleType.FIT_CENTER
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
                        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                //position과 spinner종류를 토대로 뭔가 해보는 function
                                afterItemSelected(position,spinnerType)
                            }

                        }
                    } as T
                }
                MAPVIEW -> {
                    result = map().mMap as T
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
                            userName = dataSnapshot.child("name").value.toString()
                            userPhoneNum = dataSnapshot.child("phoneNum").value.toString()
                            it!!.findNavController().navigate(R.id.nav_booking)    /*fragment 전환*/
                            tryID_Token_Sync(ID_Token(id,MainActivity.pushToken!!))
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

        fun trySignInManager(id : String ="",pw : String="",it : View?){ /*이더리움으로 부터 "client"->상태 고객 고유 ID와 true값을 받아 고유ID를 반환함*/
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
                            tryID_Token_Sync(ID_Token(managerInfo!!.name,MainActivity.pushToken!!))
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
                            dataSnapshot.child("info").value.toString(),
                            dataSnapshot.child("lat").value.apply{} as Double,
                            dataSnapshot.child("lng").value.apply{} as Double
                        )
                        dataSnapshot.key -> companyInfo=CompanyInfo(
                            dataSnapshot.child("id").value.toString(),
                            dataSnapshot.child("name").value.toString(),
                            dataSnapshot.child("address").value.toString(),
                            dataSnapshot.child("phoneNumber").value.toString(),
                            dataSnapshot.child("imageSrc").value.toString(),
                            dataSnapshot.child("info").value.toString(),
                            dataSnapshot.child("lat").value.apply{} as Double,
                            dataSnapshot.child("lng").value.apply{} as Double
                        )
                        else -> {
                            if(ID == dataSnapshot.child("id").value.toString())
                            { companyInfo=CompanyInfo(
                                dataSnapshot.child("id").value.toString(),
                                dataSnapshot.child("name").value.toString(),
                                dataSnapshot.child("address").value.toString(),
                                dataSnapshot.child("phoneNumber").value.toString(),
                                dataSnapshot.child("imageSrc").value.toString(),
                                dataSnapshot.child("info").value.toString(),
                                dataSnapshot.child("lat").value.apply{} as Double,
                                dataSnapshot.child("lng").value.apply{} as Double
                            )}
                        }
                    }
                    if(companyInfo != null){
                        when(isManager){
                            false->{ BookingMainFragment.DBListenerClient(companyInfo = companyInfo)  }
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
            //Thread.sleep(800)
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

        fun trySaveComp(phoneNumber : String, name : String, id : String, address : String, info : String, view : ImageView,position : LatLng) : Boolean {
            uploadImage("$name$id.jpeg",view)
            /*회원가입 manager*/
            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()
            val Lat = position.latitude
            val Lng = position.longitude
            val companyInfo = CompanyInfo(id, name, address,
                phoneNumber, "$name$id.jpeg",info,Lat,Lng)
            userValue = companyInfo.toMap() as Map<String, Object>?

            childUpdates!!["/Company_info/$name"] = userValue as Object
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

        fun tryBooking(phoneNum : String, userID : String, date : String, time : String, numberOfPerson : String) : Boolean{ // 핸드폰번호, userID(비로그인시 익명으로), 날짜, 시각, 예약인원으로 생성(+현재 상태)

            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()

            val reservationRequest = ReservationRequest(phoneNum, userID, date, time, numberOfPerson,"waiting",reservationCompName)
            userValue = reservationRequest.toMap() as Map<String, Object>?

            childUpdates!!["/ReservationRequset/" + phoneNum+ "@" + reservationCompName + "@" + date] = userValue as Object
            val uploadTask = mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            uploadTask.addOnSuccessListener {/*성공적으로 수정완료*/
                tryGetToken(reservationRequest.compName,reservationRequest, isModify = false)
            }

            return false
        }
        fun modifyBooking(request : ReservationRequest,cotext : BookingListFragment) : Boolean{ // 핸드폰번호, userID(비로그인시 익명으로), 날짜, 시각, 예약인원으로 생성(+현재 상태)

            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()
            userValue = request.toMap() as Map<String, Object>?

            childUpdates!!["/ReservationRequset/" + request.phoneNum+ "@" + request.compName + "@" + request.date] = userValue as Object
            val uploadTask = mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            uploadTask.addOnSuccessListener {/*성공적으로 수정완료*/
                if( request.state == "allowed") {
                    run {
                        val requestCopy = request.duplicate()
                        requestCopy.state = "show"
                        userValue = requestCopy.toMap() as Map<String, Object>?
                        childUpdates!!["/showNoShow/" + request.phoneNum + "@" + request.compName + "@" + request.date] =
                            userValue as Object
                        mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
                    }
                }
                else{
                    //("관리자 유저가 예약을 취소시켰을 경우에 키를가지고 노쇼블록에서 삭제해야함.")
                }
                tryGetToken(request.userID!!,request,isModify = true)
                cotext.refresh()
            }
            return false
        }

        fun modifyShowNoShow(request : ReservationRequest,cotext : NoShowManagerFragment) : Boolean{ // 핸드폰번호, userID(비로그인시 익명으로), 날짜, 시각, 예약인원으로 생성(+현재 상태)

            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()
            userValue = request.toMap() as Map<String, Object>?

            childUpdates!!["/showNoShow/" + request.phoneNum+ "@" + request.compName + "@" + request.date] = userValue as Object
            val uploadTask = mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            uploadTask.addOnSuccessListener {/*성공적으로 수정완료*/
                cotext.refresh()
            }
            return false
        }

        fun afterItemSelected(index : Int, spinnerType : String){/*리스너로 부터 이 녀석이 실행됨*/
            /*spinner의 종류를 파악하고*/
            /*index를 가지고 값을 정해준다.*/
            when(spinnerType){
                "ampm"->{
                    ampm = if(index == 0 )
                        "am"
                    else
                        "pm"
                }
                "hour"->{
                    hour = index + 1
                }
                "minute"->{
                    minute = index
                }
                "numberOfPerson"->{
                    numberOfPerson = index + 1
                }
            }
        }

        fun tryLookReservation(Name : String/*compName = key?*/) : ArrayList<CompanyInfo>{
            FirebaseDatabase.getInstance().reference.child("ReservationRequset").addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.e("ReservationRequset","key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1)
                    var request : ReservationRequest? = null
                    if(managerMode) {/*관리자 서비스*/
                        when (Name) {
                            null -> {
                                Log.e("tryLookReservation", "you must put compName")
                            }
                            else -> {
                                if (Name == dataSnapshot.child("compName").value.toString()) {
                                    request = ReservationRequest(
                                        dataSnapshot.child("phoneNum").value.toString(),
                                        dataSnapshot.child("userID").value.toString(),
                                        dataSnapshot.child("date").value.toString(),
                                        dataSnapshot.child("time").value.toString(),
                                        dataSnapshot.child("numberOfPerson").value.toString(),
                                        dataSnapshot.child("state").value.toString(),
                                        dataSnapshot.child("compName").value.toString()
                                    )
                                }
                            }
                        }
                    }
                    else{   /*고객 서비스*/
                        when (Name) {
                            null -> {
                                Log.e("tryLookReservation", "you must put compName")
                            }
                            else -> {
                                if (Name == dataSnapshot.child("userID").value.toString()) {
                                    request = ReservationRequest(
                                        dataSnapshot.child("phoneNum").value.toString(),
                                        dataSnapshot.child("userID").value.toString(),
                                        dataSnapshot.child("date").value.toString(),
                                        dataSnapshot.child("time").value.toString(),
                                        dataSnapshot.child("numberOfPerson").value.toString(),
                                        dataSnapshot.child("state").value.toString(),
                                        dataSnapshot.child("compName").value.toString()
                                    )
                                }
                            }
                        }
                    }
                    if(request != null){
                        run {
                            /*이더리움으로 부터 "client"->상태 고객 고유 ID와 true값을 받아 고유ID를 반환함*/
                            var userInfo: UserInfo? = null
                            FirebaseDatabase.getInstance().reference.child("User_info")
                                .addChildEventListener(object : ChildEventListener {
                                    override fun onChildAdded(
                                        dataSnapshot: DataSnapshot,
                                        p1: String?
                                    ) {
                                        Log.e(
                                            "trySignIn",
                                            "key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1
                                        )
                                        if (request!!.userID == dataSnapshot.key) {
                                            userInfo = UserInfo(
                                                request!!.userID,
                                                "****",
                                                dataSnapshot.child("name").value.toString(),
                                                dataSnapshot.child("age").value.toString(),
                                                dataSnapshot.child("phoneNum").value.toString()
                                            )
                                            BookingListFragment.createABlock(request!!, userInfo!!)
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
            val result = arrayList
            arrayList.clear()
            return result
        }

        fun tryID_Token_Sync(id_token : ID_Token) : Boolean{ //푸시알림을 위한 ID-Token 동기화 DB
            Log.i("tryIDTOKENSYNC",id_token.token)

            mDBReference = FirebaseDatabase.getInstance().reference
            childUpdates = HashMap()
            userValue = id_token.toMap() as Map<String, Object>?

            childUpdates!!["/ID_Token/" + id_token.id] = userValue as Object
            mDBReference!!.updateChildren(childUpdates as Map<String, Any>)
            return false
        }
        fun tryGetToken(id : String/*compName = key?*/,reservationRequest : ReservationRequest,isModify : Boolean= false) {
            Log.i("tryGetToken", "id : "+id)
            FirebaseDatabase.getInstance().reference.child("ID_Token").addChildEventListener(object:ChildEventListener{
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.e("ID_Token","key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1)
                    var idtoken : ID_Token?
                    Log.i("tryGetToken", "dataSnapshot.child_id : "+dataSnapshot.child("id").value.toString())
                    if (id == dataSnapshot.child("id").value.toString()) {
                        idtoken = ID_Token(
                            dataSnapshot.child("id").value.toString(),
                            dataSnapshot.child("token").value.toString()
                        )
                        val state = if(reservationRequest.state == "waiting")  {"대기중"}  else if(reservationRequest.state == "allowed"){"허가됨"} else{"거부됨"}
                        if(isModify)
                            FcmPush.sendMessage("예약 상태 변경","예약이 "+state+"으로 변경되었습니다.\n"+ reservationRequest.date+ "  " + reservationRequest.time + "  인원 : " + reservationRequest.numberOfPerson + "명",idtoken!!)
                        else
                            FcmPush.sendMessage("예약 요청",reservationRequest.date+ "  " + reservationRequest.time + "  인원 : " + reservationRequest.numberOfPerson + "명",idtoken!!)
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
        }

        fun tryLookShowNoShowList(Name : String/*compName = key?*/){
            Log.i("tryLookShowNoShowList", "name : "+Name)
            FirebaseDatabase.getInstance().reference.child("showNoShow").addChildEventListener(object:ChildEventListener{

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    Log.e("showNoShow","key=" + dataSnapshot.key + ", " + dataSnapshot.value + ", s=" + p1)
                    var request : ReservationRequest? = null
                    if(managerMode) {/*관리자 서비스*/
                        when (Name) {
                            null -> {
                                Log.e("tryLookShowNoShowList", "you must put compName")
                            }
                            else -> {
                                if (Name == dataSnapshot.child("compName").value.toString()) {
                                    request = ReservationRequest(
                                        dataSnapshot.child("phoneNum").value.toString(),
                                        dataSnapshot.child("userID").value.toString(),
                                        dataSnapshot.child("date").value.toString(),
                                        dataSnapshot.child("time").value.toString(),
                                        dataSnapshot.child("numberOfPerson").value.toString(),
                                        dataSnapshot.child("state").value.toString(),
                                        dataSnapshot.child("compName").value.toString()
                                    )
                                }
                            }
                        }
                    }
                    else{   /*고객 서비스*/
                        Log.i("tryLookShowNoShowList","you are not manager")
                    }
                    if(request != null){
                        NoShowManagerFragment.createABlock(request = request)
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
        }

    }
}