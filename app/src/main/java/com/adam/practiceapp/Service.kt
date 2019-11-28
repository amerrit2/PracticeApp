package com.adam.practiceapp

import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Service {
    val TAG = "SERVICE"

    companion object {
        fun makeRequest() {
            Log.e(TAG, "Making Request")
            val request = JSONObject()
            request.put("requestName", "henry")
            request.put("requestInput", JSONObject())

            return URL("https://practice-app-service.herokuapp.com/api")
                .openConnection()
                .let {
                    Log.e(TAG, "Connection opened")
                    it as HttpURLConnection
                }.apply {
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    requestMethod = "POST"

                    doOutput = true
                    val outputWriter = OutputStreamWriter(outputStream)
                    outputWriter.write(request.toString())
                    outputWriter.flush()
                    Log.e(TAG, "Post sent")
                }.let {
                    if (it.responseCode == 200) it.inputStream else it.errorStream
                }.let { streamToRead ->
                    Log.e(TAG, "Got stream")
                    BufferedReader(InputStreamReader(streamToRead)).use {
                        val response = StringBuffer()

                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                        response.toString()
                    }
                }

        }
    }
}