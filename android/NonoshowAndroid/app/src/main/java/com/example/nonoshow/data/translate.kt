package com.example.nonoshow.data

import com.example.nonoshow.MyApplication.Companion.contextForList
import com.example.nonoshow.R
import java.math.BigInteger

class translate {
    companion object    /*RSA 암호화*/
    {
        private val p = contextForList!!.resources.getString(R.string.p).toInt()
        private val q = contextForList!!.resources.getString(R.string.q).toInt()
        private val n = p*q //val pyn = (p-1)*(q-1)
        private val e = contextForList!!.resources.getString(R.string.e).toInt()
        private val d = contextForList!!.resources.getString(R.string.d).toInt()

        fun twist(str: String) : String{ /*문자열을 문자하나하나 숫자로 바꾸고 그것을 문자열로 길게 이음 깊이는 1*/
            val byteStr = ByteArray(str.length) // 문자열 갯수만큼 바이트코드로 인식함.

            // 문자열의 한문자씩 byte 단위로 byte 배열 byteStr에 저장하기.
            for (i in 0 until str.length)
                byteStr[i] = str[i].toByte() //바이트의 단위로 인식할 수 있게 형변환.

            // 자료형 문자, byte 단위의 문자 비교하기
            var result = ""
            for (value in byteStr) {  /*value를 가지고 암호화 한 뒤에 문자열로 전부 이어 붙힘*/
                val temp = execute(value.toInt().toBigInteger())  /*value(바이트화 한 문자 하나)를 암호화*/
                result += if(result =="")
                    temp
                else
                    "@$temp"  /*전부 길게 이어붙임*/
            }
            return result   /*암호화가 다 되어 저장만 하면 되는 상태가 된다.*/
        }

        fun solve(str: String) : String{    /*긴 암호화 된 문자열을 가져와서 제대로된 문자열을 뱉는다.*/
            /*문자열을 잘라서 배열화*/
            val array = str.split("@")

            /*복호화 한 뒤 Int를 char로 다시 만들고 이어붙힘*/
            var result = ""
            for(c in array){
                result += open(c).toChar()
            }

            /*return*/
            return result   /*퉷!*/
        }

        private fun execute(message : BigInteger): String{ /* 암호화 하는 녀석 깊이는 2*/
            return (message.pow(e) % n.toBigInteger()).toString()
        }

        private fun open(C : String): Int{ /*복호화 하는녀석*/
            return (C.toBigInteger().pow(d) % n.toBigInteger()).toInt()
        }
        /*
        private fun fixLen(byte : Int) : String{ /*숫자가 4자리수가 아닌경우 4자리수로 만들어주는 녀석*/
            var result = byte.toString() /*숫자를 문자열로 바꿈*/
            if(byte < 1000)  /*4자리수 이하인 경우*/
                result = "0$result"
            if(byte < 100)  /*3자리수 이하인 경우*/
                result = "0$result"
            if(byte < 10)   /*2자리수 이하인 경우에*/
                result = "0$result"
            return result
        }

        private fun neLxif(string : String) : Int{ /*문자열을 가져와서 잉여 0을 제거하고 정수를 반환하는 녀석*/
            val array : CharArray = string.toCharArray()
            while(array[0] == '0' && array.isNotEmpty()){ /*맨 처음 녀석이 0인경우*/
                for(i in 0 ..array.size-2){
                    array[i] = array[i+1] // 하나씩 당겨오기
                }
            }
            val result = array.toString() /*정수로 변환할 준비가 됨*/

            return result.toInt() /*문자열을 int형으로 변경하고 리턴*/
        }
        */

    }
}