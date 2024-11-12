package ai.ancf.lmos.arc.sample.data

data class Product(
    val name: String,
    val link:String?,
    val thumbnail: String,
    val price: String,
    val review: String
)

data class Category(
    val category: String,
    val products: List<Product>
)