package com.adam.practiceapp

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import loge
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

data class GenericResponse(val status: String, val message: String?)

class Service {
    companion object {
        private val jsonMapper = ObjectMapper()
        fun makeRequest(): GenericResponse {
            loge("Making Request")
            val request = JSONObject()
            request.put("requestName", "createUser")
            request.put("requestInput", JSONObject(
                        mapOf("username" to "harry",
                            "password" to "harrysPassword",
                            "email" to "invalidEmail")))

            loge(request.toString())

            return URL("https://practice-app-service.herokuapp.com/api")
                .openConnection()
                .let {
                    loge("Connection opened")
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
                    BufferedReader(streamToRead.reader()).use {
                        jsonMapper.readTree(streamToRead).let { node ->
                            val payload = node.get("payload")
                            GenericResponse(
                                payload.get("status").asText(),
                                payload.get("message").asText()
                            )
                        }
                    }

                }
        }
    }
}