package com.shorterurl.application

import com.shorterurl.api.UrlController
import com.shorterurl.repository.InMemoryRepository
import com.shorterurl.service.AliasUrlService
import com.shorterurl.service.ShortenUrlService
import com.shorterurl.service.UrlService
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureDependencyInjection() {
    val repository = InMemoryRepository()
    val shortenUrlService = ShortenUrlService(repository)
    val aliasUrlService = AliasUrlService(repository)
    val urlService = UrlService(shortenUrlService, aliasUrlService, repository)
    val urlController = UrlController(urlService)

    routing {
        urlController.registerRoutes(this)
    }
}
