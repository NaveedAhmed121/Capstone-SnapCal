package ca.gbc.comp3074.snapcal.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Searches Open Food Facts for foods by name and returns calorie / macro estimates.
 * No API key required.
 */
class NutritionSearchService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    suspend fun searchFood(query: String): List<NutritionResult> = try {
        val resp: OFFSearchResp = client.get("https://world.openfoodfacts.org/cgi/search.pl") {
            parameter("search_terms",  query)
            parameter("action",        "process")
            parameter("json",          "1")
            parameter("page_size",     "8")
            parameter("fields",        "product_name,nutriments,quantity,serving_size")
            parameter("sort_by",       "unique_scans_n")  // most-scanned first = most relevant
        }.body()
        resp.products
            .filter { !it.product_name.isNullOrBlank() && it.nutriments != null }
            .mapNotNull { p ->
                val nut = p.nutriments ?: return@mapNotNull null
                val kcal = nut.energy_kcal_100g ?: nut.energyKcal ?: return@mapNotNull null
                NutritionResult(
                    name     = p.product_name!!.trim(),
                    calories = kcal.toInt(),
                    protein  = nut.proteins_100g?.toInt() ?: 0,
                    carbs    = nut.carbohydrates_100g?.toInt() ?: 0,
                    fat      = nut.fat_100g?.toInt() ?: 0,
                    quantity = p.quantity ?: p.serving_size ?: "per 100g"
                )
            }
            .distinctBy { it.name.lowercase() }
            .take(6)
    } catch (_: Exception) { emptyList() }
}

data class NutritionResult(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val quantity: String
)

@Serializable
data class OFFSearchResp(val products: List<OFFProductResult> = emptyList())

@Serializable
data class OFFProductResult(
    val product_name: String? = null,
    val quantity: String? = null,
    val serving_size: String? = null,
    val nutriments: OFFNutriments? = null
)

@Serializable
data class OFFNutriments(
    val energy_kcal_100g: Double? = null,
    @kotlinx.serialization.SerialName("energy-kcal") val energyKcal: Double? = null,
    val proteins_100g: Double? = null,
    val carbohydrates_100g: Double? = null,
    val fat_100g: Double? = null
)
