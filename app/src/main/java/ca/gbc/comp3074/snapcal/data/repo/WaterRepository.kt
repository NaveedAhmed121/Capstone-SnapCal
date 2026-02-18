package ca.gbc.comp3074.snapcal.data.repo

import ca.gbc.comp3074.snapcal.data.db.WaterDao
import ca.gbc.comp3074.snapcal.data.model.WaterEntry

class WaterRepository(private val dao: WaterDao) {

    val entries = dao.observeAll()
    val todayTotalMl = dao.observeTodayTotalMl()

    suspend fun add(amountMl: Int) {
        if (amountMl <= 0) return
        dao.insert(WaterEntry(amountMl = amountMl))
    }
}
