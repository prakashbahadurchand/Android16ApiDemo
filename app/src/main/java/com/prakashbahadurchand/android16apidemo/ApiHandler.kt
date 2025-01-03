package com.prakashbahadurchand.android16apidemo

import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL

enum class ApiMethod {
    GET, POST, PUT, DELETE, PATCH
}


class ApiHandler<T>(private val callback: (T?, String?) -> Unit, private val type: Type) {

    fun fetchData(urlString: String, method: ApiMethod, body: String? = null) {
        FetchDataTask(callback, method, body, type).execute(urlString)
    }

    private class FetchDataTask<T>(
        private val callback: (T?, String?) -> Unit,
        private val method: ApiMethod,
        private val body: String?,
        private val type: Type
    ) : AsyncTask<String, Void, Pair<T?, String?>>() {

        override fun doInBackground(vararg params: String): Pair<T?, String?> {
            val urlString = params[0]
            var result: T? = null
            var error: String? = null

            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = method.name
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                if (method == ApiMethod.POST || method == ApiMethod.PUT || method == ApiMethod.PATCH) {
                    connection.doOutput = true
                    body?.let {
                        val outputStream = connection.outputStream
                        val writer = OutputStreamWriter(outputStream, "UTF-8")
                        writer.write(it)
                        writer.flush()
                        writer.close()
                        outputStream.close()
                    }
                }

                connection.connect()

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val stringBuilder = StringBuilder()

                    reader.use {
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                    }

                    val jsonResponse = stringBuilder.toString()
                    val jsonElement = JsonParser.parseString(jsonResponse)
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val formattedJson = gson.toJson(jsonElement)
                    Log.d("ApiHandler", "------------------------------------------------------------------------------------------------")
                    Log.d("ApiHandler", "URL: $urlString\nResponse:\n$formattedJson")
                    Log.d("ApiHandler", "------------------------------------------------------------------------------------------------")
                    result = gson.fromJson(jsonResponse, type)
                } else {
                    error = "HTTP error code: $responseCode"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = e.message
            }

            return Pair(result, error)
        }

        override fun onPostExecute(result: Pair<T?, String?>) {
            super.onPostExecute(result)
            callback(result.first, result.second)
        }
    }
}