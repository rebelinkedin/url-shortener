package com.shorterurl.repository

import java.util.concurrent.ConcurrentHashMap

class InMemoryRepository {

    private val shortUrlToLongUrlStorage = ConcurrentHashMap<String, String>()
    private val longUrlToShortUrlStorage = ConcurrentHashMap<String, MutableList<String>>()

    fun save(shortUrl: String, longUrl: String) {
        shortUrlToLongUrlStorage.putIfAbsent(shortUrl, longUrl)
        val existingShortUrls = longUrlToShortUrlStorage.getOrPut(longUrl) { mutableListOf() }
        existingShortUrls.add(shortUrl)
    }

    fun findByShortUrl(shortUrl: String): String? {
        return shortUrlToLongUrlStorage[shortUrl]
    }

    fun findByLongUrl(originalUrl: String): List<String>? {
        return longUrlToShortUrlStorage[originalUrl]
    }
}
