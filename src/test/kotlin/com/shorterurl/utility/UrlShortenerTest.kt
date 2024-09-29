package com.shorterurl.utility

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlShortenerTest {

    @Test
    fun testCreateShortUrl() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val shortUrl1 = UrlShortener.create(originalUrl)
        val shortUrl2 = UrlShortener.create(originalUrl)
        val shortUrl3 = UrlShortener.create(originalUrl)
        val shortUrls = listOf(shortUrl1, shortUrl2, shortUrl3)

        assertTrue { shortUrls.all { it.length == 8 } }
        assertTrue { shortUrls.all { UrlShortener.check(it) } }
    }

    @Test
    fun testValidAlias() {
        val validAlias = listOf("000000", "a1B2C3", "a1B2C3dEs312", "000000000000")

        validAlias.forEach {
            assertTrue(UrlShortener.check(it))
        }
    }

    @Test
    fun testInvalidAlias() {
        val invalidAlias = listOf("1", "103m5", "-1234567890", "%wq9@swrLcq2")

        invalidAlias.forEach {
            assertFalse(UrlShortener.check(it))
        }
    }
}
