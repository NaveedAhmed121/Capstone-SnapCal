package ca.gbc.comp3074.snapcal.data.remote
import io.ktor.client.*; import io.ktor.client.call.*; import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*; import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*; import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
class OpenFoodFactsService {
    private val client = HttpClient(CIO) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
    suspend fun getProduct(barcode: String): ProductResponse? = try {
        client.get("https://world.openfoodfacts.org/api/v0/product/$barcode.json").body()
    } catch (_: Exception) { null }
}
@Serializable data class ProductResponse(val product: Product? = null, val status: Int = 0)
@Serializable data class Product(val product_name: String? = null, val nutriments: Nutriments? = null)
@Serializable data class Nutriments(
    val energy_kcal_100g: Double? = null, val proteins_100g: Double? = null,
    val carbohydrates_100g: Double? = null, val fat_100g: Double? = null
)
