package com.shorterurl.service

import com.shorterurl.model.AliasUrlRequest
import com.shorterurl.model.AlreadyExistsException
import com.shorterurl.model.InvalidFormatException
import com.shorterurl.repository.InMemoryRepository
import com.shorterurl.utility.UrlShortener

class AliasUrlService(
    private val inMemoryRepository: InMemoryRepository
) {
    fun useAliasForShortUrl(request: AliasUrlRequest): String {
        val (alias, longUrl) = request

        validateAliasFormat(alias)
        checkIfAlreadyBeenUsed(alias, longUrl)

        inMemoryRepository.save(alias, longUrl)
        return alias
    }

    private fun validateAliasFormat(alias: String) {
        val correctFormat = UrlShortener.check(alias)
        if (!correctFormat) {
            throw InvalidFormatException("Alias '$alias' format is invalid; it must be at least 6 characters long.")
        }
    }

    private fun checkIfAlreadyBeenUsed(alias: String, longUrl: String) {
        val existingLongUrl = inMemoryRepository.findByShortUrl(alias)
        if (existingLongUrl != null && existingLongUrl != longUrl) {
            throw AlreadyExistsException("Alias '$alias' already exists for another long URL.")
        }
    }
}
