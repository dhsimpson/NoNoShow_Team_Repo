package com.example.nonoshow;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class NoshowModel {
    String result = "err";
    String urls_;
    int gender_;
    int age_;
    String date_;
    int sms_received_;
    /***
     * 만들어진 모듈을 불러와서 머신러닝 예측을 실행 하기 위한 클래스
     * 어플리케이션 내부에 저장된 모듈을 불러오게 된다 - 업데이트를 통해 교체가능
     * json 이된 모델로부터 가중치벡터, 상수항을 가져와 예측하도록 한다.
     */
   Thread flaskThread = new Thread(new Runnable(){
        @Override
        public void run() {
            boolean failed = true;
            while(failed){
                try {
                    int scheduledDay_DOW = getDayOfWeek(date_);
                    OkHttpClient client = new OkHttpClient.Builder()
                            .retryOnConnectionFailure(true)
                            .build();

                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("gender", String.valueOf(gender_));
                    jsonInput.put("age", String.valueOf(age_));
                    jsonInput.put("scheduledDay_DOW", String.valueOf(scheduledDay_DOW));
                    jsonInput.put("sms_received", String.valueOf(sms_received_));
                    Log.i("test", jsonInput.toString());

                    RequestBody reqBody = RequestBody.create(
                            MediaType.parse("application/json; charset=utf-8"),
                            jsonInput.toString()
                    );

                    Request request = new Request.Builder()
                            .addHeader("Connection","close")
                            .addHeader("content-type", "application/json")
                            .post(reqBody)
                            .url(urls_)
                            .build();
                    Response responses = client.newCall(request).execute();
                    String message = "no message";
                    if (responses.body() != null) {
                        message = responses.body().string();
                    }
                    Log.i("res",message);

                    JSONObject jObject = new JSONObject(message);
                    result = jObject.getString("predict");

                    System.out.println("" + message);
                    failed = false;
                    responses.close();
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    failed = true;
                }
            }
        }

    });
    public String noshowPredict(String urls, int gender, int age, String date, int sms_received) {
        urls_ = urls;
        gender_ = gender;
        age_ = age;
        date_ = date;
        sms_received_ = sms_received;
        flaskThread.start();
        try {
            flaskThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
    public int getDayOfWeek(String date){
        String[] strArray = date.split(" ");
        String year = strArray[0].split("년")[0];
        String month = strArray[1].split("월")[0];
        String day = strArray[2].split("일")[0];
        month = String.valueOf(Integer.valueOf(month) - 1);
        day = String.valueOf(Integer.valueOf(day) - 1);
//        if (month.length() <2){
//            month = "0"+month;
//        }
//        if (day.length()<2){
//            day = "0"+day;
//        }
        date = day+"/"+month+"/"+year;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        Date date_ = null;
        try {
            date_ = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c= Calendar.getInstance();
        c.setTime(date_);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1){dayOfWeek+=7;}
        dayOfWeek -= 2;
        return dayOfWeek;
    }
}
