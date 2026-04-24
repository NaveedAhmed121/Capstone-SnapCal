package ca.gbc.comp3074.snapcal.data.repo
import ca.gbc.comp3074.snapcal.data.db.WaterDao
import ca.gbc.comp3074.snapcal.data.model.WaterEntry
class WaterRepository(private val dao: WaterDao) {
    val entries = dao.observeAll()
    val todayTotalMl = dao.observeTodayTotalMl()
    fun observeWaterByDay(sinceMillis: Long) = dao.observeWaterByDay(sinceMillis)
    suspend fun add(amountMl: Int) { if (amountMl > 0) dao.insert(WaterEntry(amountMl = amountMl)) }
    suspend fun subtract(amountMl: Int) { if (amountMl > 0) dao.insert(WaterEntry(amountMl = -amountMl)) }
}
