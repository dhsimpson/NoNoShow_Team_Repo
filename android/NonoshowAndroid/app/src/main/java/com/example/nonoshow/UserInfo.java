package com.example.nonoshow;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {
    public String id;
    public String pw;
    public String name;
    public String age;
    public String phoneNum;

    public UserInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public UserInfo(String id, String pw, String name, String age,
                     String phoneNum) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.age = age;
        this.phoneNum = phoneNum;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw", pw);
        result.put("name", name);
        result.put("age", age);
        result.put("phoneNum", phoneNum);

        return result;
    }
}