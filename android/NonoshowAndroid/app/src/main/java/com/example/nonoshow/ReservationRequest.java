package com.example.nonoshow;

import java.util.HashMap;
import java.util.Map;

public class ReservationRequest {
    public String phoneNum;
    public String userID;
    public String date;
    public String time;
    public String numberOfPerson;
    public String state;
    public String compName;

    public ReservationRequest(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public ReservationRequest(String phoneNum, String userID, String date, String time,
                    String numberOfPerson, String state, String compName) {
        this.phoneNum = phoneNum;
        this.userID = userID;
        this.date = date;
        this.time = time;
        this.numberOfPerson = numberOfPerson;
        this.state = state;
        this.compName = compName;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("phoneNum", phoneNum);
        result.put("userID", userID);
        result.put("date", date);
        result.put("time", time);
        result.put("numberOfPerson", numberOfPerson);
        result.put("state",state);
        result.put("compName", compName);

        return result;
    }
}