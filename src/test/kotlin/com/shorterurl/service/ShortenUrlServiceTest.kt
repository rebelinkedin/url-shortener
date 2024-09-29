package com.shorterurl.service

import com.shorterurl.model.ShortenUrlRequest
import com.shorterurl.repository.InMemoryRepository
import com.shorterurl.utility.UrlShortener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShortenUrlServiceTest {

    private val repository = InMemoryRepository()
    private val shortenService = ShortenUrlService(repository)

    @Test
    fun testShortenUrl() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"

        val actualShortUrl = shortenService.shortenOriginalUrl(ShortenUrlRequest(originalUrl))
        val expectedShortUrl = UrlShortener.create(originalUrl)

        assertEquals(expectedShortUrl, actualShortUrl)
        assertTrue { repository.findByShortUrl(expectedShortUrl) == originalUrl }
    }

    @Test
    fun testShortenUrlWhenShortUrlAlreadyExists() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val request = ShortenUrlRequest(originalUrl)
        shortenService.shortenOriginalUrl(request)
        shortenService.shortenOriginalUrl(request)

        val actualShortUrl = shortenService.shortenOriginalUrl(request)
        val expectedShortUrl = UrlShortener.create(originalUrl)

        assertEquals(expectedShortUrl, actualShortUrl)
        assertTrue { repository.findByShortUrl(expectedShortUrl) == originalUrl }
    }
}
