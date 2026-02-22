package ca.gbc.comp3074.snapcal.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OpenFoodFactsService {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getProduct(barcode: String): ProductResponse? {
        return try {
            client.get("https://world.openfoodfacts.org/api/v0/product/$barcode.json").body<ProductResponse>()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

@Serializable
data class ProductResponse(
    val product: Product? = null,
    val status: Int = 0
)

@Serializable
data class Product(
    val product_name: String? = null,
    val nutriments: Nutriments? = null
)

@Serializable
data class Nutriments(
    val energy_kcal_100g: Double? = null,
    val proteins_100g: Double? = null,
    val carbohydrates_100g: Double? = null,
    val fat_100g: Double? = null
)
