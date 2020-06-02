package com.example.nonoshow

import android.util.Log

import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import kotlinx.coroutines.Dispatchers

import org.json.JSONException
import org.json.JSONObject

import java.util.Objects

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.util.Base64
import com.example.nonoshow.MyApplication.Companion.contextForList
import okhttp3.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class AWSService {
    internal var jsonObject: JSONObject? = null
    internal var title: String? = null
    internal var content: String? = null
    internal var TAG = "AWSService"
    internal var imgsrc: String? = null
    internal var bucketEndPoint: String? = null
    internal var imgFile: File? = null
    internal var srcSplit: Array<String>? = null


    internal var idx: Int = 0

    fun downlodImageS3() {
        try {
            //TODO( 일해라 노예야 )

            if (jsonObject!!.has("IMGSRC")) {
                imgsrc = jsonObject!!.getString("IMGSRC")
                srcSplit = imgsrc!!.split("\\/".toRegex(), 6).toTypedArray()
                bucketEndPoint = srcSplit!![srcSplit!!.size - 1]
                Log.i("endpoint", bucketEndPoint)

                /**S3Thread = new AmazonS3Thread();
                 * S3Thread.start(); */
            }
            idx = Integer.parseInt(jsonObject!!.getString("IDX"))

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        /**if(jsonObject.has("IMGSRC")) {
         * try {
         * S3Thread.join();
         * Log.i("succ", observer.toString());
         *
         *
         * } catch (InterruptedException e) {
         * e.printStackTrace();
         * }
         * } */


        val credentialsProvider = CognitoCachingCredentialsProvider(
            Objects.requireNonNull(MyApplication.contextForList),
            "", // Identity Pool ID
            Regions.US_WEST_2 // Region
        )
        val s3 = AmazonS3Client(credentialsProvider)
        val transferUtility = TransferUtility.builder()
            .context(Objects.requireNonNull(MyApplication.contextForList))
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(s3)
            .build()
        //createImageFile(imgsrc);
        val observer = transferUtility.download(
            /*observer가 0이면 성공 0이상이면 에러*/

            "www.pkdximagebucket.com", /* 다운로드 할 버킷 이름 */
            "images/$bucketEndPoint", /* 키 */
            imgFile/* 어디에 저장할것인지 */
        )
        observer!!.setTransferListener(object : TransferListener {

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    //TODO( 이미지가 올라가면 ? 할일 )
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()

                Log.d(
                    TAG,
                    "   ID:$id   bytesCurrent: $bytesCurrent   bytesTotal: $bytesTotal $percentDone%"
                )
            }

            override fun onError(id: Int, ex: Exception) {
                // Handle errors
                Log.e(TAG, "Unable to download the file.", ex)
            }
        })
        s3.setRegion(Region.getRegion(Regions.US_WEST_2))
        s3.endpoint = "s3.us-west-2.amazonaws.com"
    }
    companion object {
        fun uploadImageS3(filePath : String) {
            val uploadUrl = contextForList!!.resources.getString(R.string.imageUploadURLForS3)
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
                .addFormDataPart("file","image Name.png",RequestBody.create(MultipartBody.FORM,File(filePath)))
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

            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
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
    }
}