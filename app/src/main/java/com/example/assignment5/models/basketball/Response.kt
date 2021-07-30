package com.example.assignment5.models.basketball

data class Response(
    val country: Country,
    val date: String,
    val id: Int,
    val league: League,
    val scores: Scores,
    val stage: Any,
    val status: Status,
    val teams: Teams,
    val time: String,
    val timestamp: Int,
    val timezone: String,
    val week: Any
)