package com.shorterurl.utility

import com.shorterurl.model.Url
import com.shorterurl.model.Url.Companion.BASE_URL
import java.net.URI

fun Url.transform() = Url("$BASE_URL${this.value}")

fun Url.isValid(): Boolean {
    return try {
        val uri = URI(this.value)
        uri.scheme != null && uri.host != null
    } catch (e: Exception) {
        false
    }
}

fun Url.isValidShortUrl(): Boolean {
    var shortUrl = this.value
    if (shortUrl.startsWith(BASE_URL)) {
        shortUrl = shortUrl.substringAfterLast('/')
    }
    return UrlShortener.check(shortUrl)
}
