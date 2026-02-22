package ca.gbc.comp3074.snapcal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.gbc.comp3074.snapcal.data.model.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {

    @Insert
    suspend fun insert(meal: MealLog)

    @Query("SELECT * FROM meal_logs ORDER BY createdAt DESC")
    fun observeAllMeals(): Flow<List<MealLog>>

    @Query("""
        SELECT COALESCE(SUM(calories), 0) 
        FROM meal_logs 
        WHERE date(createdAt/1000, 'unixepoch', 'localtime') = date('now', 'localtime')
    """)
    fun observeTodayCalories(): Flow<Int>

    @Query("""
        SELECT date(createdAt/1000, 'unixepoch', 'localtime') as day, SUM(calories) as calories
        FROM meal_logs
        WHERE createdAt >= :sinceMillis
        GROUP BY day
        ORDER BY day
    """)
    fun observeCaloriesByDay(sinceMillis: Long): Flow<List<DayTotal>>
}
