package com.example.assignment5

//type:1 야구, 2 축구, 3 농구
data class Game(val type:Int = 0, val teamId: Int = -1, val shortName: String,
                val fullName: String, val nickName: String, val score: Int = 0)
