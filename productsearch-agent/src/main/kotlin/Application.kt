// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.sample

import ai.ancf.lmos.arc.sample.utils.ProductSearch
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@SpringBootApplication
class ProductsearchAgentApplication

fun main(args: Array<String>) {
    runApplication<ProductsearchAgentApplication>(*args)
}


//To-DO[Only for the testing]
@Component
class AppInitializer(
    private val environment: Environment
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        ProductSearch.initialize(environment)
    }
}