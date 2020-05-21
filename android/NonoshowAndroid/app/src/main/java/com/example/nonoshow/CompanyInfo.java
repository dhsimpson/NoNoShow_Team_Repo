package com.example.nonoshow;

import java.util.HashMap;
import java.util.Map;

public class CompanyInfo {
    public static String _default = "default";
    public String id;
    public String name;
    public String address;
    public String phoneNum;
    public String imageSrc;
    public String info;

    public CompanyInfo(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public CompanyInfo(String id, String name, String address,
                       String phoneNum,String imageSrc,String info) {
        this.id = id;   /*manager id*/
        this.name = name;   /*comp name*/
        this.address = address;
        this.phoneNum = phoneNum;
        this.imageSrc = imageSrc;
        this.info = info;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("address", address);
        result.put("phoneNum", phoneNum);
        result.put("imageSrc",imageSrc);
        result.put("info",info);

        return result;
    }
}
