package com.example.nonoshow

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.nonoshow.MyApplication.Companion.contextForList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class ec2Connection {

    internal var jsonObject: JSONObject? = null
    internal var title: String? = null
    internal var content: String? = null
    internal var TAG = "AWSService"
    internal var imgsrc: String? = null
    internal var bucketEndPoint: String? = null
    internal var imgFile: File? = null
    internal var srcSplit: Array<String>? = null


    internal var idx: Int = 0
    companion object {
        fun uploadImageS3(filePath : String) {
            val uploadUrl = MyApplication.contextForList!!.resources.getString(R.string.imageUploadURLForS3)
            GlobalScope.launch(Dispatchers.IO/*main = main, default = computation,*/) {
                val result = post(uploadUrl, filePath)
                GlobalScope.launch(Dispatchers.Main) {
                    Log.i("s3 upload",result)
                }
            }
        }
        val JSON = MediaType.parse("application/json; charset=utf-8")

        var client = OkHttpClient()

        @Throws(IOException::class)
        fun post(url: String, filePath : String): String {
            val multipartBuilder = MultipartBody.Builder("----WebKitFormBoundaryBMoZLED3nBXvJAFA")
                .setType(MultipartBody.FORM)
                .addFormDataPart("file","image Name.png",
                    RequestBody.create(MultipartBody.FORM, File(filePath)))
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val body = multipartBuilder.build()
            val request = Request.Builder()
                .url(url)
                .post(body)
                .tag("androidUpload")
                .build()
            val response = client.newCall(request).execute()
            return response.body()!!.string()
        }

        private fun createJson(bitmap : Bitmap) : String{
            return getStringFromBitmap(bitmap)
        }

        private fun getStringFromBitmap(bitmapPicture : Bitmap) : String  {
            val byteArrayBitmapStream = ByteArrayOutputStream()
            bitmapPicture.compress(Bitmap.CompressFormat.PNG, 100, byteArrayBitmapStream)

            return Base64.encodeToString(byteArrayBitmapStream.toByteArray(), Base64.DEFAULT)
        }

        fun post2(urls: String, json: String): String{
            val url = URL(urls)
            val lineEnd = "\r\n"
            val twoHyphens = "--"
            val boundary = "s3uploadlasdiufjh"
            val connection = url.openConnection() as HttpURLConnection
            connection.useCaches = false // 캐시 사용 안 함
            connection.requestMethod = "POST" //전송방식
            connection.doOutput = true       //데이터를 쓸 지 설정
            connection.doInput = true        //데이터를 읽어올지 설정

            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty(
                "Host",
                urls
            ) //permission 가져오기

            /*gps = new GpsInfo(MainActivity.this);*/
            /*if (gps.isGetLocation()) {
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        jsonParam.put("LAT", latitude);
                        jsonParam.put("LON", longitude);
                    }
                    else{
                        showSettingsAlert();
                    }*/
            val os = OutputStreamWriter(connection.outputStream)

            os.write(json+boundary) //전송
            os.close()
            val `is`: InputStream

            val sb = StringBuilder()
            if (connection.responseCode >= 400) {
                `is` = connection.errorStream
            } else {
                `is` = connection.inputStream
            }
            val br = BufferedReader(InputStreamReader(`is`, "utf-8"))

            var line: String
            var page = ""
            for (line in br.readLines()) { //기존의 br.String()을 바꿈.
                page += line
            }
            var result = ""
            try {
                val json : JSONObject = JSONObject("{$page}")
                Log.i("json", json.toString())
                result = json.getString("summery")

                println(result)

            } catch (e: Exception) {

                e.printStackTrace()

            }

            //Result = result //받아온 결과를 글로벌 변수에 저장해 다른 Activity 에서 사용하도록 함.

            Log.i("STATUS", connection.responseCode.toString())
            Log.i("MSG", connection.responseMessage)
            // 200인경우 정상 403 permission error 404 not found 413 type error
            connection.disconnect()
            return result
        }
        fun httpcall(urlString : String, jsonParam : JSONObject) : String{
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.useCaches = false // 캐시 사용 안 함
            connection.requestMethod = "POST" //전송방식
            connection.doOutput = true       //데이터를 쓸 지 설정
            connection.doInput = true        //데이터를 읽어올지 설정
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Accept", "application/form-data")

            Log.i("json", jsonParam.toString())
            val os = OutputStreamWriter(connection.outputStream)

            os.write(jsonParam.toString()) //전송
            os.close()
            val `is`: InputStream

            val sb = StringBuilder()
            if (connection.responseCode >= 400) {
                `is` = connection.errorStream
            } else {
                `is` = connection.inputStream
            }
            val br = BufferedReader(InputStreamReader(`is`, "utf-8"))

            var line : String?
            var page = ""
            line = br.readLine()
            while (line != null) { //기존의 br.String()을 바꿈.
                page += line
                line = br.readLine()
            }
            var result = ""
            /*try {
                        JSONObject json = new JSONObject(page);
                        Log.i("json",json.toString());
                        result = json.getString("summery");

                        System.out.println(result);

                    } catch (Exception e) {

                        e.printStackTrace();

                    }*/
            result = page
            Log.e("result",result)

            Log.i("STATUS", connection.responseCode.toString())
            Log.i("MSG", connection.responseMessage)
            // 200인경우 정상 403 permission error 404 not found 413 type error
            connection.disconnect()
            return result
        }
    }
}