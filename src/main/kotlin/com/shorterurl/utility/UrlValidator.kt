package com.shorterurl.utility

import java.net.URI

object UrlValidator {

    fun checkLongUrl(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.scheme != null && uri.host != null
        } catch (e: Exception) {
            false
        }
    }
}
