package ca.gbc.comp3074.snapcal.data.db
import androidx.room.*
import ca.gbc.comp3074.snapcal.data.model.WorkoutEntry
import kotlinx.coroutines.flow.Flow
@Dao
interface WorkoutDao {
    @Insert suspend fun insert(entry: WorkoutEntry)
    @Delete suspend fun delete(entry: WorkoutEntry)
    @Query("SELECT * FROM workout_entries ORDER BY dateMillis DESC") fun observeAll(): Flow<List<WorkoutEntry>>
    @Query("SELECT COALESCE(SUM(caloriesBurned),0) FROM workout_entries WHERE isScheduled=0 AND date(dateMillis/1000,'unixepoch','localtime')=date('now','localtime')") fun observeTodayBurned(): Flow<Int>
    @Query("SELECT date(dateMillis/1000,'unixepoch','localtime') as day,SUM(caloriesBurned) as calories FROM workout_entries WHERE isScheduled=0 AND dateMillis>=:sinceMillis GROUP BY day ORDER BY day") fun observeBurnedByDay(sinceMillis: Long): Flow<List<DayTotal>>
    @Query("SELECT * FROM workout_entries WHERE isScheduled=1 AND dateMillis>=:nowMillis ORDER BY dateMillis ASC") fun observeUpcoming(nowMillis: Long): Flow<List<WorkoutEntry>>
}
