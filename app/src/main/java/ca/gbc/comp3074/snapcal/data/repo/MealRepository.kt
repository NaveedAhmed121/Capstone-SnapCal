package ca.gbc.comp3074.snapcal.data.repo

import ca.gbc.comp3074.snapcal.data.db.DayTotal
import ca.gbc.comp3074.snapcal.data.db.MealLogDao
import ca.gbc.comp3074.snapcal.data.model.MealLog
import kotlinx.coroutines.flow.Flow

class MealRepository(private val dao: MealLogDao) {
    fun observeMeals(): Flow<List<MealLog>> = dao.observeAllMeals()
    fun observeTodayCalories(): Flow<Int> = dao.observeTodayCalories()
    fun observeCaloriesByDay(sinceMillis: Long): Flow<List<DayTotal>> = dao.observeCaloriesByDay(sinceMillis)

    suspend fun addMeal(meal: MealLog) = dao.insert(meal)
}
