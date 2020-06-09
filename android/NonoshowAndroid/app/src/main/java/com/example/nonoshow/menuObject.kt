package com.example.nonoshow

class menuObject(var numberOfMenu: Int){
    val stuffs = Array<Stuff>(numberOfMenu){Stuff("notFound",-1)}
    val count = 0
    fun addStuff(name : String, price:Int) : Boolean{   /*음식정보 한개 추가*/
        if(count < numberOfMenu) {
            stuffs[count] = Stuff(name, price)
            return true
        }
        else
            return false
    }
    fun findStuffByName(name : String) : Stuff{ /*이름으로 음식 한개 가져오기*/
        for(i in 0..(numberOfMenu-1))
            if(name == stuffs[i].name)
                return stuffs[i]
        return Stuff("notFound",-1) /*찾는 녀석이 없으면 이놈을 출력*/
    }
    fun getPriceByName(name : String) : Int{    /*이름으로 메뉴 가격가져오기*/
        return findStuffByName(name).price
    }
    class Stuff(var name: String, var price: Int)
}