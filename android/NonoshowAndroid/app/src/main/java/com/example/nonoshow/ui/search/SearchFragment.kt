package com.example.nonoshow.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.nonoshow.R
import kotlin.random.Random

class SearchFragment : Fragment() {

    private lateinit var searchViewModel: SearchViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val edittext : EditText = getView()!!.findViewById(R.id.Text_search)
        val circleImageButton : Button = getView()!!.findViewById(R.id.circleImageButton)
        val bookText : TextView = getView()!!.findViewById(R.id.text_book)
        val book = "원을 터치해서 예약기록"
        val unBook ="원을 터치해서 예약기록취소"
        var isBooked = false
        val title = "핸드폰번호로 검색"

        (activity as AppCompatActivity).supportActionBar?.title = title

        circleImageButton.textSize = 25f
        circleImageButton.text = ""

        /******************************************************/
        edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var phoneNum = s.toString()
                /*textView.text = phoneNum*/

                if(phoneNum.length > 10){
                    circleImageButton.text = search(phoneNum)
                    bookText.text=book
                    isBooked = false
                }
            }
        })
        /******************************************************/
        circleImageButton.setOnClickListener {
            /*원이 클릭이 됐을경우*/
            if(isBooked) {
                bookText.text = unBook
                isBooked = false
                /*텍스트, 원의 색 변경해보기*/
            }
            else if(!isBooked){
                bookText.text = book //보이는 text 변경
                isBooked = true //toggle 을 위한 저장
                /*텍스트, 원의 색 변경해보기*/
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProviders.of(this).get(SearchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_search_by_phone_num, container, false)
        return root
    }

    fun search(num : String) : String{
        // 여기에 검색함수
        val randomValue = Random.nextInt(0,100)
        return setTextInCircle(randomValue)
    }

    private fun setTextInCircle(x : Int): String{
        return x.toString() + "%"
    }
}