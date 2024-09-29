package com.shorterurl.service

import com.shorterurl.model.AliasUrlRequest
import com.shorterurl.model.AlreadyExistsException
import com.shorterurl.model.InvalidFormatException
import com.shorterurl.repository.InMemoryRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AliasUrlServiceTest {

    private val repository = InMemoryRepository()
    private val aliasService = AliasUrlService(repository)

    @Test
    fun testUseAliasForShortUrl() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val alias = "xxxxxxxx"

        val actualShortUrl = aliasService.useAliasForShortUrl(AliasUrlRequest(alias, originalUrl))

        assertEquals(alias, actualShortUrl)
        assertTrue { repository.findByShortUrl(actualShortUrl) == originalUrl }
    }

    @Test
    fun testUseAliasForShortUrlWithAlreadyExistsAlias() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val alias = "5n6vmwmr"
        val request = AliasUrlRequest(alias, originalUrl)
        aliasService.useAliasForShortUrl(request)
        aliasService.useAliasForShortUrl(request)

        val actualShortUrl = aliasService.useAliasForShortUrl(request)

        assertEquals(alias, actualShortUrl)
        assertTrue { repository.findByShortUrl(actualShortUrl) == originalUrl }
    }

    @Test
    fun testUseExistingAliasForDifferentOriginalUrl() {
        val originalUrl1 = "https://example.com/this/is/a/very/ver/very/long/url"
        val alias = "5n6vmwmr"
        aliasService.useAliasForShortUrl(AliasUrlRequest(alias, originalUrl1))

        assertFailsWith<AlreadyExistsException>("Alias '$alias' already exists for another long URL.") {
            val originalUrl2 = "https://example.com/this/is/a/different/url"
            aliasService.useAliasForShortUrl(AliasUrlRequest(alias, originalUrl2))
        }
    }

    @Test
    fun testUseInvalidAliasForShortUrl() {
        val originalUrl = "https://example.com/this/is/a/very/ver/very/long/url"
        val alias = "aaa"

        assertFailsWith<InvalidFormatException>("Alias $alias format is invalid, at least 8 characters") {
            aliasService.useAliasForShortUrl(AliasUrlRequest(alias, originalUrl))
        }
    }
}
