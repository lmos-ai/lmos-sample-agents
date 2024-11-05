// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package ai.ancf.lmos.arc.sample.utils

import ai.ancf.lmos.arc.sample.data.ApiResponse
import ai.ancf.lmos.arc.sample.data.Product
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class ProductSearch() {
    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)
        private lateinit var searchEngineKey: String
        private lateinit var cloudApiKey: String

        //This function will extract products based on query using custom google search engine
        fun searchProduct(query: String, siteSearch: String): List<Product> {
            // Initialize the Ktor client
            val client = HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true }) // Configure JSON deserialization
                }
            }
            return runBlocking {
                try {
                    // Make the GET request and parse JSON into ProductResponse
                    val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
                    logger.info("Query to search: $query")
                    val url =
                        "https://www.googleapis.com/customsearch/v1?key=$cloudApiKey=$searchEngineKey=$encodedQuery&googlehost=google.com&lr=lang_en&alt=json"
                    val response: ApiResponse = client.get(url).body()
                    val productList = response.items.map { item ->
                        Product(
                            name = item.title,
                            siteName = item.displayLink,
                            link = item.link,
                            snippet = item.snippet,
                            thumbnail = item.pagemap.cse_thumbnail?.get(0)?.src,
                            imageUrl = item.pagemap.cse_image?.get(0)?.src,
                            rating = item.pagemap.aggregaterating,
                            metadata = item.pagemap.metatags,
                            offer = item.pagemap.offer
                        )
                    }
                    logger.info(productList.toString())
                    productList
                } catch (e: Exception) {
                    println("Error fetching product data: ${e.message}")
                    emptyList()
                } finally {
                    client.close()
                }
            }
        }

        fun initialize(environment: Environment) {
            searchEngineKey = environment.getProperty("google.search.engine.key", "")
            cloudApiKey = environment.getProperty("google.cloud.api.key", "")
        }
    }
}