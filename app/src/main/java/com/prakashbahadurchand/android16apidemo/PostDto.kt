package com.prakashbahadurchand.android16apidemo

import java.io.Serializable

data class PostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
) : Serializable