package com.example.nonoshow;

import java.util.HashMap;
import java.util.Map;

public class ManagerInfo {
    public static String _default = "default";
    public String id;
    public String pw;
    public String name;
    public String address;
    public String phoneNum;
    public String imageSrc;
    public String info;

    public ManagerInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public ManagerInfo(String id, String pw, String name, String address,
                    String phoneNum) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.address = address;
        this.phoneNum = phoneNum;
        this.imageSrc = _default;
        this.info = _default;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw", pw);
        result.put("name", name);
        result.put("address", address);
        result.put("phoneNum", phoneNum);
        result.put("imageSrc",imageSrc);
        result.put("info",info);

        return result;
    }
}
