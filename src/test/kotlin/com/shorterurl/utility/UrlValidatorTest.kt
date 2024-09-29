package com.shorterurl.utility

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UrlValidatorTest {

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
        validUrls.forEach { assertTrue { UrlValidator.checkLongUrl(it) } }
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

        invalidUrls.forEach { assertFalse { UrlValidator.checkLongUrl(it) } }
    }
}
