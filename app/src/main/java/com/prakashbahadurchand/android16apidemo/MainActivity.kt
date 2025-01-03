package com.prakashbahadurchand.android16apidemo

import android.app.Activity
import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import com.google.gson.reflect.TypeToken
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

        binding.btnCallApi.setOnClickListener {
            // For single PostDto
            ApiHandler<PostDto>({ post, error ->
                if (error != null) {
                    binding.tvResponse.text = "Error: $error"
                } else if (post != null) {
                    binding.tvResponse.text = "Title: ${post.title}\n\nBody: ${post.body}"
                } else {
                    binding.tvResponse.text = "Failed to fetch data"
                }
            }, PostDto::class.java).fetchData("https://jsonplaceholder.typicode.com/posts/1", ApiMethod.GET)

            // For list of PostDto
            val listType = object : TypeToken<List<PostDto>>() {}.type
            ApiHandler<List<PostDto>>({ posts, error ->
                if (error != null) {
                    binding.tvResponse.text = "Error: $error"
                } else if (posts != null) {
                    val stringBuilder = StringBuilder()
                    for (post in posts) {
                        stringBuilder.append("Title: ${post.title}\nBody: ${post.body}\n\n")
                    }
                    binding.tvResponse.text = stringBuilder.toString()
                } else {
                    binding.tvResponse.text = "Failed to fetch data"
                }
            }, listType).fetchData("https://jsonplaceholder.typicode.com/posts", ApiMethod.GET)
        }
    }

}