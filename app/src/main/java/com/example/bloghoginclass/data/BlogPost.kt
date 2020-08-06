package com.example.bloghoginclass.data

import com.google.firebase.Timestamp

data class BlogPost (
    val userId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val title: String = "",
    val subheading: String = "",
    val body: String =""
)