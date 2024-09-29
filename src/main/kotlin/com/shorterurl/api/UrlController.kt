package com.shorterurl.api

import com.shorterurl.model.AlreadyExistsException
import com.shorterurl.model.InvalidFormatException
import com.shorterurl.service.UrlService
import com.shorterurl.utility.UrlValidator
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.Created
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

@Serializable
data class ShortenUrlRequestDto(val longUrl: String, val alias: String? = null)

@Serializable
data class ShortenUrlResponseDto(val shortUrl: String)

@Serializable
data class GetOriginalUrlResponseDto(val shortUrl: String)

class UrlController(
    private val urlService: UrlService
) {
    fun registerRoutes(route: Route) {
        route.apply {
            postShortenUrl(this)
            getOriginalUrl(this)
        }
    }

    private fun postShortenUrl(route: Route) {
        route.post("$BASE_PATH/shorten") {
            val request = call.receive<ShortenUrlRequestDto>()
            val isValidUrl = UrlValidator.checkLongUrl(request.longUrl)
            if (isValidUrl.not()) {
                return@post call.respond(BadRequest, "Invalid URL format.")
            }

            try {
                val shortUrl = urlService.createShortUrl(request)
                call.respond(Created, ShortenUrlResponseDto("$BASE_URL${shortUrl.value}"))
            } catch (exception: Exception) {
                when (exception) {
                    is InvalidFormatException -> call.respond(BadRequest, exception.message.toString())
                    is AlreadyExistsException -> call.respond(Conflict, exception.message.toString())
                    else -> call.respond(InternalServerError)
                }
            }
        }
    }

    private fun getOriginalUrl(route: Route) {
        route.get("$BASE_PATH/{shortUrl}") {
            val shortUrl = call.parameters["shortUrl"]
                ?: return@get call.respond(BadRequest, "Short URL parameter is missing.")

            val originalUrl = urlService.getOriginalUrl(shortUrl)
                ?: return@get call.respond(HttpStatusCode.NotFound, "URL not found.")

            call.respond(OK, GetOriginalUrlResponseDto(originalUrl.value))
        }
    }

    companion object {
        const val BASE_PATH = "/api/v1/urls"
        const val BASE_URL = "https://shorturl.com/"
    }
}
