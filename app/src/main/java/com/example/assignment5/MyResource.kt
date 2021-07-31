package com.example.assignment5

data class MyResource(
    val leagueName: String,
    val homeName: String,
    val awayName: String,
    val date: String,
    val time: String,
    val homeTotal: Int,
    val awayTotal: Int,
    var type: Int,
    var remaining: Long,
    var isChecked: ArrayList<Boolean> = arrayListOf(false, false, false, false, false, false, false, false),
    )
