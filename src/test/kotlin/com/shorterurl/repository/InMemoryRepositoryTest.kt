package com.shorterurl.repository

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InMemoryRepositoryTest {

    private val urlRepository = InMemoryRepository()

    @Test
    fun testSaveShortUrlWithLongUrl() {
        val shortUrl = "shortUrl"
        val originalUrl = "https://longurl.com/"
        urlRepository.save(shortUrl, originalUrl)

        assertTrue { urlRepository.findByShortUrl(shortUrl) == originalUrl }
        assertTrue {
            val existingShortUrls = urlRepository.findByLongUrl(originalUrl)
            existingShortUrls != null && existingShortUrls.size == 1
            existingShortUrls!!.contains(shortUrl)
        }
    }

    @Test
    fun testSaveShortUrlWithDifferentLongUrls() {
        val shortUrl = "shorturl"
        val originalUrl1 = "https://longurl.com/1/"
        val originalUrl2 = "https://longurl.com/2/"
        urlRepository.save(shortUrl, originalUrl1)
        urlRepository.save(shortUrl, originalUrl2)

        assertTrue { urlRepository.findByShortUrl(shortUrl) == originalUrl1 }
        assertTrue {
            val existingShortUrls = urlRepository.findByLongUrl(originalUrl1)
            existingShortUrls != null && existingShortUrls.size == 1
            existingShortUrls!!.contains(shortUrl)
        }
    }

    @Test
    fun testSaveDifferentShortUrlsWithSameLongUrl() {
        val shortUrl1 = "12345678"
        val shortUrl2 = "22345678"
        val originalUrl = "https://longurl.com/"
        urlRepository.save(shortUrl1, originalUrl)
        urlRepository.save(shortUrl2, originalUrl)

        assertTrue { urlRepository.findByShortUrl(shortUrl1) == originalUrl }
        assertTrue { urlRepository.findByShortUrl(shortUrl2) == originalUrl }
        assertTrue {
            val existingShortUrls = urlRepository.findByLongUrl(originalUrl)
            existingShortUrls != null && existingShortUrls.size == 2
            existingShortUrls!!.containsAll(listOf(shortUrl1, shortUrl2))
        }
    }

    @Test
    fun testConcurrentSave() {
        val shortUrl = "shortUrl"
        val originalUrl = "originalUrl"

        repeat(10) {
            val executors = Executors.newFixedThreadPool(5)
            for (i in 0 until 5) {
                executors.submit {
                    urlRepository.save(shortUrl, originalUrl)
                }
            }

            executors.shutdown()
            executors.awaitTermination(10, TimeUnit.SECONDS)

            assertTrue { urlRepository.findByShortUrl(shortUrl) == originalUrl }
            assertTrue {
                val existingShortUrls = urlRepository.findByLongUrl(originalUrl)
                existingShortUrls != null && existingShortUrls.size == 1
                existingShortUrls!!.contains(shortUrl)
            }        }
    }
}
