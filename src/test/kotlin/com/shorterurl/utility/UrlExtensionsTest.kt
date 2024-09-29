package com.shorterurl.utility

import com.shorterurl.model.Url
import com.shorterurl.model.Url.Companion.BASE_URL
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlExtensionsTest {

    @Test
    fun testTransform() {
        val shortUrl = "abcdf738"
        val actualShortUrl = Url(shortUrl).transform()

        assertTrue { actualShortUrl.value == "https://shorterurl.com/$shortUrl" }
    }

    @Test
    fun testValidUrl() {
        val validUrls = listOf(
            "http://example.com",
            "https://example.com",
            "ftp://example.com",
            "http://example.com?query=param",
            "http://example.com#section",
            "http://example.com:8080"
        )

        validUrls.forEach {
            assertTrue { Url(it).isValid() }
        }
    }

    @Test
    fun testInvalidUrl() {
        val invalidUrls = listOf(
            "",
            "example.com",
            "http://",
            "/path/to/resource",
            "://example.com",
            "ht@t://example?.com"
        )

        invalidUrls.forEach {
            assertFalse { Url(it).isValid() }
        }
    }

    @Test
    fun testValidShortUrl() {
        val validUrls = listOf(
            "123456",
            "1234567",
            "afsd24",
            "e324uewr21i302112rj",
            "${BASE_URL}afsd24",
            "${BASE_URL}2u43i2uiu32i"
        )

        validUrls.forEach {
            assertTrue { Url(it).isValidShortUrl() }
        }
    }

    @Test
    fun testInvalidShortUrl() {
        val invalidUrls = listOf(
            "",
            "1",
            "123",
            "·$21443",
            "${BASE_URL}123",
            "${BASE_URL}@#¢"
        )

        invalidUrls.forEach {
            assertFalse { Url(it).isValidShortUrl() }
        }
    }
}
