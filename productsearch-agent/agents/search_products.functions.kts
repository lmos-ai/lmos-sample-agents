import io.github.lmos.arc.runner.utils.ProductSearch

function(
    name = "search_products",
    description = "Search products based on the specifications",
    params = types(
        string(
            name = "query",
            description = "A query with technical descriptions of the products or user required specifications"
        )
    ),
) { (query) ->
    val contextResult = query?.let { ProductSearch.searchProduct(it, "") }
    """
        $contextResult
    """.trimIndent()
}

