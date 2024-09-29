package com.shorterurl.service

import com.shorterurl.api.ShortenUrlRequestDto
import com.shorterurl.model.AliasUrlRequest
import com.shorterurl.model.ShortenUrlRequest
import com.shorterurl.model.Url
import com.shorterurl.repository.InMemoryRepository

class UrlService(
    private val shortenService: ShortenUrlService,
    private val aliasService: AliasUrlService,
    private val inMemoryRepository: InMemoryRepository
) {
    fun createShortUrl(request: ShortenUrlRequestDto): Url {
        val (longUrl, alias) = request

        return if (alias != null) {
            Url(value = handleAlias(longUrl, alias))
        } else {
            Url(value = shortenService.shortenOriginalUrl(ShortenUrlRequest(longUrl)))
        }
    }

    fun getOriginalUrl(shortUrl: String): Url? {
        return inMemoryRepository.findByShortUrl(shortUrl)?.let { Url(value = it) }
    }

    private fun handleAlias(longUrl: String, alias: String): String {
        val existingShortUrl = getExistingShortUrl(longUrl, alias)
        return existingShortUrl ?: aliasService.useAliasForShortUrl(AliasUrlRequest(alias, longUrl))
    }

    private fun getExistingShortUrl(longUrl: String, shortUrl: String): String? {
        val urls = inMemoryRepository.findByLongUrl(longUrl) ?: return null
        return urls.find { it == shortUrl }
    }
}
