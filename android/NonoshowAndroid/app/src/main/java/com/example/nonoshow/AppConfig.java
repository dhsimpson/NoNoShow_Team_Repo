package com.example.nonoshow;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AppConfig extends AppCompatActivity {
    public static String contractAddres = EthereumServiceKt.Companion.getContractAddress();
    public String contractABI = getJsonString();

    private String getJsonString() {
        String json = "";

        try {
            InputStream is = getAssets().open("NoNoShow.json");
            int fileSize = is.available();

            byte[] buffer = new byte[fileSize];
            is.read(buffer);
            is.close();

            json = new String(buffer, StandardCharsets.UTF_8);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return json;
    }
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

}
