package ca.gbc.comp3074.snapcal.data.remote
import io.ktor.client.*; import io.ktor.client.call.*; import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*; import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*; import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

data class OnlineRecipe(
    val id: String, val name: String, val category: String, val area: String,
    val thumbnail: String, val instructions: String,
    val ingredients: List<Pair<String,String>>  // name to measure
)

class TheMealDbService {
    private val client = HttpClient(CIO) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
    private val base = "https://www.themealdb.com/api/json/v1/1"

    suspend fun searchByName(query: String): List<OnlineRecipe> = try {
        val resp: MealListResponse = client.get("$base/search.php") { parameter("s", query) }.body()
        (resp.meals ?: emptyList()).map { it.toOnlineRecipe() }
    } catch (_: Exception) { emptyList() }

    suspend fun searchByIngredient(ingredient: String): List<OnlineRecipe> = try {
        val resp: MealListResponse = client.get("$base/filter.php") { parameter("i", ingredient) }.body()
        val ids = (resp.meals ?: emptyList()).take(5).map { it.idMeal ?: "" }.filter { it.isNotEmpty() }
        ids.mapNotNull { id ->
            try {
                val detail: MealListResponse = client.get("$base/lookup.php") { parameter("i", id) }.body()
                detail.meals?.firstOrNull()?.toOnlineRecipe()
            } catch (_: Exception) { null }
        }
    } catch (_: Exception) { emptyList() }
}

private fun MealDto.toOnlineRecipe(): OnlineRecipe {
    val ings = mutableListOf<Pair<String,String>>()
    for (i in 1..20) {
        val ingName = getField("strIngredient$i")?.trim()
        if (ingName.isNullOrEmpty()) continue
        val measure = getField("strMeasure$i")?.trim() ?: ""
        ings.add(Pair(ingName, measure))
    }
    return OnlineRecipe(
        id = idMeal ?: "", name = strMeal ?: "Unknown",
        category = strCategory ?: "", area = strArea ?: "",
        thumbnail = strMealThumb ?: "", instructions = strInstructions ?: "",
        ingredients = ings
    )
}

private fun MealDto.getField(name: String): String? = when(name) {
    "strIngredient1"->strIngredient1;"strIngredient2"->strIngredient2;"strIngredient3"->strIngredient3
    "strIngredient4"->strIngredient4;"strIngredient5"->strIngredient5;"strIngredient6"->strIngredient6
    "strIngredient7"->strIngredient7;"strIngredient8"->strIngredient8;"strIngredient9"->strIngredient9
    "strIngredient10"->strIngredient10;"strIngredient11"->strIngredient11;"strIngredient12"->strIngredient12
    "strIngredient13"->strIngredient13;"strIngredient14"->strIngredient14;"strIngredient15"->strIngredient15
    "strIngredient16"->strIngredient16;"strIngredient17"->strIngredient17;"strIngredient18"->strIngredient18
    "strIngredient19"->strIngredient19;"strIngredient20"->strIngredient20
    "strMeasure1"->strMeasure1;"strMeasure2"->strMeasure2;"strMeasure3"->strMeasure3
    "strMeasure4"->strMeasure4;"strMeasure5"->strMeasure5;"strMeasure6"->strMeasure6
    "strMeasure7"->strMeasure7;"strMeasure8"->strMeasure8;"strMeasure9"->strMeasure9
    "strMeasure10"->strMeasure10;"strMeasure11"->strMeasure11;"strMeasure12"->strMeasure12
    "strMeasure13"->strMeasure13;"strMeasure14"->strMeasure14;"strMeasure15"->strMeasure15
    "strMeasure16"->strMeasure16;"strMeasure17"->strMeasure17;"strMeasure18"->strMeasure18
    "strMeasure19"->strMeasure19;"strMeasure20"->strMeasure20
    else->null
}

@Serializable data class MealListResponse(val meals: List<MealDto>? = null)
@Serializable data class MealDto(
    val idMeal: String? = null, val strMeal: String? = null, val strCategory: String? = null,
    val strArea: String? = null, val strInstructions: String? = null, val strMealThumb: String? = null,
    val strIngredient1: String?=null,val strIngredient2: String?=null,val strIngredient3: String?=null,
    val strIngredient4: String?=null,val strIngredient5: String?=null,val strIngredient6: String?=null,
    val strIngredient7: String?=null,val strIngredient8: String?=null,val strIngredient9: String?=null,
    val strIngredient10: String?=null,val strIngredient11: String?=null,val strIngredient12: String?=null,
    val strIngredient13: String?=null,val strIngredient14: String?=null,val strIngredient15: String?=null,
    val strIngredient16: String?=null,val strIngredient17: String?=null,val strIngredient18: String?=null,
    val strIngredient19: String?=null,val strIngredient20: String?=null,
    val strMeasure1: String?=null,val strMeasure2: String?=null,val strMeasure3: String?=null,
    val strMeasure4: String?=null,val strMeasure5: String?=null,val strMeasure6: String?=null,
    val strMeasure7: String?=null,val strMeasure8: String?=null,val strMeasure9: String?=null,
    val strMeasure10: String?=null,val strMeasure11: String?=null,val strMeasure12: String?=null,
    val strMeasure13: String?=null,val strMeasure14: String?=null,val strMeasure15: String?=null,
    val strMeasure16: String?=null,val strMeasure17: String?=null,val strMeasure18: String?=null,
    val strMeasure19: String?=null,val strMeasure20: String?=null
)
