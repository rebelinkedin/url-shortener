package com.shorterurl.utility

import java.security.MessageDigest
import kotlin.math.absoluteValue

object UrlShortener {
    private const val CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val BASE = CHARACTERS.length
    private const val SHORT_URL_LENGTH = 8
    private const val MIN_LENGTH = 6

    fun create(originalUrl: String): String {
        val hash = hashUrl(originalUrl).absoluteValue
        return encodeToBase62(hash).take(SHORT_URL_LENGTH)
    }

    fun check(shortUrl: String): Boolean {
        return shortUrl.length >= MIN_LENGTH && shortUrl.all { CHARACTERS.contains(it) }
    }

    private fun hashUrl(url: String): Long {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(url.toByteArray())
        return digest.fold(0L) { acc, byte -> acc * 256 + (byte.toInt() and 0xFF) }
    }

    private fun encodeToBase62(number: Long): String {
        val sb = StringBuilder()
        var num = number

        while (num > 0) {
            sb.append(CHARACTERS[(num % BASE).toInt()])
            num /= BASE
        }
        return sb.reverse().toString()
    }
}
