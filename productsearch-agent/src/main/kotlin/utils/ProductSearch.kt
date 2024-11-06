// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package ai.ancf.lmos.arc.sample.utils

import ai.ancf.lmos.arc.sample.data.ApiResponse
import ai.ancf.lmos.arc.sample.data.Product
import com.nimbusds.jose.shaded.gson.Gson
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
        private val gson = Gson()
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
                    var productList = emptyList<Product>()
                    if (cloudApiKey.isNotEmpty() && searchEngineKey.isNotEmpty()) {
                        val url =
                            "https://www.googleapis.com/customsearch/v1?key=$cloudApiKey&cx=$searchEngineKey&q=$encodedQuery&googlehost=google.com&lr=lang_en&alt=json"
                        val response: ApiResponse = client.get(url).body()
                        //meta-data is huge which might cause token issue with llm so extract only price and currency
                        val regex = Regex("(price|currency)")
                        productList = response.items.map { item ->
                            Product(
                                name = item.title,
                                siteName = item.displayLink,
                                link = item.link,
                                snippet = item.snippet,
                                thumbnail = item.pagemap.cse_thumbnail?.get(0)?.src,
                                imageUrl = item.pagemap.cse_image?.get(0)?.src,
                                rating = item.pagemap.aggregaterating,
                                metadata = extractRequiredMetaData(item.pagemap.metatags, regex),
                                offer = item.pagemap.offer
                            )
                        }
                    }
                    //print response in json
                    logger.debug(gson.toJson(productList))
                    productList
                } catch (e: Exception) {
                    println("Error fetching product data: ${e.message}")
                    emptyList()
                } finally {
                    client.close()
                }
            }
        }

        //this function extract only required or mandatory information from meta-tags
        private fun extractRequiredMetaData(
            metatags: List<Map<String, String>>?, regex: Regex
        ): List<Map<String, String>>? {
            val extractedMetatags = mutableListOf<Map<String, String>>()
            if (metatags != null) {
                for (map in metatags) {
                    for ((key, value) in map) {
                        if (regex.containsMatchIn(key)) {
                            // If regex matches the key, add it to the new list
                            extractedMetatags.add(mapOf(key to value))
                        }
                    }
                }
            }
            return extractedMetatags
        }

        fun initialize(environment: Environment) {
            searchEngineKey = environment.getProperty("google.search.engine.key", "")
            cloudApiKey = environment.getProperty("google.cloud.api.key", "")
        }
    }
}