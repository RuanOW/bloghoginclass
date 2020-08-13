package com.example.bloghoginclass.data

import com.google.firebase.Timestamp

data class BlogPost (
    var id: String = "",
    val userId: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val headerImageUrl: String = "",
    val title: String = "",
    val subheading: String = "",
    val body: String =""
)