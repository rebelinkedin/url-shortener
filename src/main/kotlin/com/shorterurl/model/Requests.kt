package com.shorterurl.model

data class ShortenUrlRequest(
    val originalUrl: String
)

data class AliasUrlRequest(
    val alias: String,
    val originalUrl: String
)
