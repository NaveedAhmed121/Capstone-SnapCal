package ca.gbc.comp3074.snapcal.data.repo
import ca.gbc.comp3074.snapcal.data.db.MealLogDao
import ca.gbc.comp3074.snapcal.data.model.MealLog
class MealRepository(private val dao: MealLogDao) {
    fun observeMeals() = dao.observeAllMeals()
    fun observeTodayCalories() = dao.observeTodayCalories()
    fun observeCaloriesByDay(sinceMillis: Long) = dao.observeCaloriesByDay(sinceMillis)
    suspend fun addMeal(meal: MealLog) = dao.insert(meal)
    suspend fun deleteMeal(meal: MealLog) = dao.delete(meal)
    suspend fun addMeal(name:String,calories:Int,protein:Int,carbs:Int,fat:Int,mealType:String,cuisine:String,goalType:String) =
        dao.insert(MealLog(name=name.trim(),calories=calories,protein=protein,carbs=carbs,fat=fat,mealType=mealType,cuisine=cuisine,goalType=goalType))
}
