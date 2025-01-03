package com.prakashbahadurchand.android16apidemo

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import com.prakashbahadurchand.android16apidemo.databinding.ActivityMainBinding
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCallApi.setOnClickListener { view ->
            FetchDataTask(binding.tvResponse).execute("https://jsonplaceholder.typicode.com/posts/1")
        }
    }

    private class FetchDataTask(private val resultTextView: TextView) : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String? {
            val urlString = params[0]
            var result: String? = null

            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
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

                    result = stringBuilder.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                try {
                    val jsonObject = JSONObject(result)
                    val title = jsonObject.getString("title")
                    val body = jsonObject.getString("body")
                    resultTextView.text = "Title: $title\n\nBody: $body"
                } catch (e: Exception) {
                    e.printStackTrace()
                    resultTextView.text = "Error parsing response"
                }
            } else {
                resultTextView.text = "Failed to fetch data"
            }
        }
    }

}