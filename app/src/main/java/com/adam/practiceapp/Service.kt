package com.adam.practiceapp

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Service {
    companion object {
        fun makeRequest() {
            val request = JSONObject()
            request.put("requestName", "henry")
            request.put("requestInput", JSONObject())

            return URL("http://localhost:5000")
                .openConnection()
                .let {
                    it as HttpURLConnection
                }.apply {
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    requestMethod = "POST"

                    doOutput = true
                    val outputWriter = OutputStreamWriter(outputStream)
                    outputWriter.write(request.toString())
                    outputWriter.flush()
                }.let {
                    if (it.responseCode == 200) it.inputStream else it.errorStream
                }.let { streamToRead ->
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