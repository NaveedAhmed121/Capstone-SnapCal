package ca.gbc.comp3074.snapcal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ca.gbc.comp3074.snapcal.data.model.WaterEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {

    @Insert
    suspend fun insert(entry: WaterEntry)

    @Query("SELECT * FROM water_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<WaterEntry>>

    // Total for "today" in local time
    @Query(
        """
        SELECT COALESCE(SUM(amountMl), 0)
        FROM water_entries
        WHERE date(createdAt/1000, 'unixepoch', 'localtime') = date('now', 'localtime')
        """
    )
    fun observeTodayTotalMl(): Flow<Int>

    @Query("""
        SELECT date(createdAt/1000, 'unixepoch', 'localtime') as day, SUM(amountMl) as calories
        FROM water_entries
        WHERE createdAt >= :sinceMillis
        GROUP BY day
        ORDER BY day ASC
    """)
    fun observeWaterByDay(sinceMillis: Long): Flow<List<DayTotal>>

    @Query("DELETE FROM water_entries")
    suspend fun clearAll()
}
