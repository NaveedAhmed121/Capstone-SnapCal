package ca.gbc.comp3074.snapcal.data.repo
import ca.gbc.comp3074.snapcal.data.db.WorkoutDao
import ca.gbc.comp3074.snapcal.data.model.WorkoutEntry
class WorkoutRepository(private val dao: WorkoutDao) {
    fun observeAll() = dao.observeAll()
    fun observeTodayBurned() = dao.observeTodayBurned()
    fun observeBurnedByDay(sinceMillis: Long) = dao.observeBurnedByDay(sinceMillis)
    fun observeUpcoming(nowMillis: Long) = dao.observeUpcoming(nowMillis)
    suspend fun add(entry: WorkoutEntry) = dao.insert(entry)
    suspend fun delete(entry: WorkoutEntry) = dao.delete(entry)
}
