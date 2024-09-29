package com.shorterurl.service

import com.shorterurl.model.ShortenUrlRequest
import com.shorterurl.repository.InMemoryRepository
import com.shorterurl.utility.UrlShortener

class ShortenUrlService(
    private val urlRepository: InMemoryRepository
) {
    fun shortenOriginalUrl(request: ShortenUrlRequest): String {
        val longUrl = request.originalUrl
        val shortUrl = UrlShortener.create(longUrl)
        urlRepository.save(shortUrl, longUrl)
        return shortUrl
    }
}
