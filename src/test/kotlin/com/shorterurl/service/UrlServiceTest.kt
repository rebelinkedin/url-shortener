package com.shorterurl.service

import com.shorterurl.api.ShortenUrlRequestDto
import com.shorterurl.repository.InMemoryRepository
import com.shorterurl.utility.UrlShortener
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UrlServiceTest {

    private val inMemoryRepository = InMemoryRepository()
    private val shortenService = ShortenUrlService(inMemoryRepository)
    private val aliasService = AliasUrlService(inMemoryRepository)
    private val urlService = UrlService(shortenService, aliasService, inMemoryRepository)

    @Test
    fun testCreateShortUrlWithoutAlias() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"

        val actualShortUrl = urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, null)).value
        val expectedShortUrl = UrlShortener.create(originalUrl)

        assertEquals(expectedShortUrl, actualShortUrl)
        assertTrue { inMemoryRepository.findByShortUrl(actualShortUrl) == originalUrl }
    }

    @Test
    fun testCreateShortUrlWithAlias() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val alias = "1y7dswse"

        val actualShortUrl = urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, alias)).value

        assertEquals(alias, actualShortUrl)
    }

    @Test
    fun testCreateShortUrlWithExistingShortUrlAsAlias() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, alias = "12345678")).value

        val shortUrl = urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, null)).value
        val actualShortUrl = urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, alias = shortUrl)).value

        assertEquals(shortUrl, actualShortUrl)
    }

    @Test
    fun testGetOriginalUrlByShortenedUrl() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val shortUrl = UrlShortener.create(originalUrl)

        urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, null))
        val actualOriginalUrl = urlService.getOriginalUrl(shortUrl)?.value

        assertEquals(originalUrl, actualOriginalUrl)
    }

    @Test
    fun testGetOriginalUrlByAlias() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val alias = "1y7dswse"

        urlService.createShortUrl(ShortenUrlRequestDto(originalUrl, alias))
        val actualOriginalUrl = urlService.getOriginalUrl(alias)?.value

        assertEquals(originalUrl, actualOriginalUrl)
    }

    @Test
    fun testGetOriginalUrlWhenThereIsNoShortUrl() {
        val shortUrl = "1234567"
        val actualOriginalUrl = urlService.getOriginalUrl(shortUrl)

        assertEquals(null, actualOriginalUrl)
    }
}
