package com.example.nonoshow;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NoshowModel {
    /***
     * 만들어진 모듈을 불러와서 머신러닝 예측을 실행 하기 위한 클래스
     * 어플리케이션 내부에 저장된 모듈을 불러오게 된다 - 업데이트를 통해 교체가능
     * json 이된 모델로부터 가중치벡터, 상수항을 가져와 예측하도록 한다.
     */
    private JSONObject jsonObject = null;
    public String noshowPredict(String urls, int gender, int age, int scheduledDay_DOW, int sms_received) {
        String title="err";
        try {
            OkHttpClient client = new OkHttpClient();

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("gender", String.valueOf(gender));
            jsonInput.put("age", String.valueOf(age));
            jsonInput.put("scheduledDay_DOW", String.valueOf(scheduledDay_DOW));
            jsonInput.put("sms_received", String.valueOf(sms_received));
            Log.i("test", jsonInput.toString());

            RequestBody reqBody = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    jsonInput.toString()
            );

            Request request = new Request.Builder()
                    .post(reqBody)
                    .url(urls)
                    .build();

            Response responses = null;
            responses = client.newCall(request).execute();

            String message = responses.body().string();
            JSONObject jObject = new JSONObject(message);
            title = jObject.getString("predict");

            System.out.println("" + message);



        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return title;
    }
}
