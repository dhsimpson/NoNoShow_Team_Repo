package com.example.nonoshow;

import java.util.HashMap;
import java.util.Map;

public class ID_Token{
    public String id;
    public String token;

    public ID_Token(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public ID_Token(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("token", token);

        return result;
    }
}
