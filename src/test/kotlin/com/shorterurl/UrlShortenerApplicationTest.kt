package com.shorterurl

import com.google.gson.Gson
import com.shorterurl.api.GetOriginalUrlResponseDto
import com.shorterurl.api.ShortenUrlRequestDto
import com.shorterurl.api.ShortenUrlResponseDto
import com.shorterurl.utility.UrlShortener
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class UrlShortenerApplicationTest {
    private val baseUrl = "https://shorturl.com/"
    private val longUrl = "https://example.com/this/is/a/very/very/very/very/long/url"
    private val gson = Gson()

    @Test
    fun testPostShortenUrlWithoutAlias() = testApplication {
        val shortUrl = UrlShortener.create(longUrl)
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + shortUrl)
            assertEquals(expectedShortUrl, actualShortUrl)
        }
    }

    @Test
    fun testPostShortenUrlMultipleTimesWithoutAlias() = testApplication {
        val shortUrl = UrlShortener.create(longUrl)
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + shortUrl)
            assertEquals(expectedShortUrl, actualShortUrl)
        }
    }

    @Test
    fun testPostShortenUrlWithAlias() = testApplication {
        val alias = "2y3dswse"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, alias))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + alias)
            assertEquals(expectedShortUrl, actualShortUrl)
        }
    }

    @Test
    fun testPostShortenUrlMultipleTimesWithSameAlias() = testApplication {
        val shortUrl = UrlShortener.create(longUrl)
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + shortUrl)
            assertEquals(expectedShortUrl, actualShortUrl)
        }
    }

    @Test
    fun testPostShortenDifferentUrlWithSameAlias() = testApplication {
        val longUrl1 = "https://example.com/this/is/a/very/very/very/very/long/url"
        val longUrl2 = "https://example.com/this/is/a/long/url"
        val alias = "2y3dswse"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl1, alias))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + alias)
            assertEquals(expectedShortUrl, actualShortUrl)
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl2, alias))
        }.apply {
            assertEquals(HttpStatusCode.Conflict, status, "Alias '$alias' already exists for another long URL.")
        }
    }

    @Test
    fun testPostShortenUrlMultipleTimesWithDifferentAlias() = testApplication {
        val alias1 = "2y3dswse"
        val alias2 = "98ydx7wc"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, alias1))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + alias1)
            assertEquals(expectedShortUrl, actualShortUrl)
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, alias2))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + alias2)
            assertEquals(expectedShortUrl, actualShortUrl)
        }
    }

    @Test
    fun testPostShortenUrlWithAnExistingShortUrlAsAlias() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        val response = client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }
        val shortUrl =
            gson.fromJson(response.bodyAsText(), ShortenUrlResponseDto::class.java).shortUrl.substringAfterLast("/")

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, alias = shortUrl))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)

            val actualShortUrl = gson.fromJson(bodyAsText(), ShortenUrlResponseDto::class.java)
            val expectedShortUrl = ShortenUrlResponseDto(baseUrl + shortUrl)
            assertEquals(expectedShortUrl, actualShortUrl)
        }
    }

    @Test
    fun testShortenUrlWithInvalidOriginalUrl() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto("invalid_url", null))
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status, "Invalid URL format.")
        }
    }

    @Test
    fun testPostShortenUrlWithInvalidAlias() = testApplication {
        val alias = "1234"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, alias))
        }.apply {
            assertEquals(
                HttpStatusCode.BadRequest,
                status,
                "Alias '$alias' format is invalid; it must be at least 6 characters long."
            )
        }
    }

    @Test
    fun testGetOriginalUrlByShortUrl() = testApplication {
        val shortUrl = UrlShortener.create(longUrl)
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }

        client.get("/api/v1/urls/$shortUrl").apply {
            val responseBody = bodyAsText()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(
                expected = GetOriginalUrlResponseDto(longUrl),
                actual = gson.fromJson(responseBody, GetOriginalUrlResponseDto::class.java)
            )
        }
    }

    @Test
    fun testGetOriginalUrlByAlias() = testApplication {
        val alias = "abc1ef2h"
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, alias))
        }

        client.get("/api/v1/urls/$alias").apply {
            val responseBody = bodyAsText()
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(
                expected = GetOriginalUrlResponseDto(longUrl),
                actual = gson.fromJson(responseBody, GetOriginalUrlResponseDto::class.java)
            )
        }
    }

    @Test
    fun testGetOriginalUrlByNonExistingShortUrl() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        client.post("/api/v1/urls/shorten") {
            header(HttpHeaders.ContentType, Json)
            setBody(ShortenUrlRequestDto(longUrl, null))
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
        }

        client.get("/api/v1/urls/non-existing-url").apply {
            assertEquals(HttpStatusCode.NotFound, status, "URL not found")
        }
    }
}
