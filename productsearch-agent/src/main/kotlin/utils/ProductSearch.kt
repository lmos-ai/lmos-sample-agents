package io.github.lmos.arc.runner.utils

import io.github.lmos.arc.runner.data.ApiResponse
import io.github.lmos.arc.runner.data.Product
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ProductSearch {
    companion object {
        private val logger = LoggerFactory.getLogger(javaClass)

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
                        "https://www.googleapis.com/customsearch/v1?key=AIzaSyAyLe8LJsDQKSxKaPKfAZW4Wki5Z-N7ap8&cx=87a3a8db0fac54e56&q=$encodedQuery&googlehost=google.com&lr=lang_en&alt=json"
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
    }
}