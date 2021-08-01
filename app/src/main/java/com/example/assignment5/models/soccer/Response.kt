package com.example.assignment5.models.soccer

data class Response(
    val bookmakers: List<Bookmaker>,
    val fixture: Fixture,
    val league: League,
    val update: String
)