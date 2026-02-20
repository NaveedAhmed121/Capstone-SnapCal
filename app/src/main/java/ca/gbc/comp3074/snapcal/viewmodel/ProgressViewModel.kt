package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.ViewModel
import ca.gbc.comp3074.snapcal.data.db.DayTotal
import ca.gbc.comp3074.snapcal.data.repo.MealRepository
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class ProgressViewModel(private val mealRepo: MealRepository, private val waterRepo: WaterRepository) : ViewModel() {

    /**
     * ✅ Always returns exactly [daysCount] days, filling missing days with 0.
     * It asks the repo for totals since the start of (today - daysCount + 1).
     */
    fun lastNDaysCaloriesFilled(daysCount: Int): Flow<List<DayTotal>> {
        val zone = ZoneId.systemDefault()

        // Start date = (today - (daysCount - 1)) at 00:00
        val startDate = LocalDate.now().minusDays((daysCount - 1).toLong())
        val startOfStartDateMillis = startDate
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        // Keys we want to show in UI (yyyy-MM-dd)
        val keys = (0 until daysCount).map { i ->
            LocalDate.now().minusDays((daysCount - 1L - i)).toString()
        }

        // ✅ Pass sinceMillis to repo
        return mealRepo.observeCaloriesByDay(sinceMillis = startOfStartDateMillis).map { raw ->
            val map = raw.associateBy { it.day } // yyyy-MM-dd -> DayTotal

            keys.map {
                map[it] ?: DayTotal(day = it, calories = 0)
            }
        }
    }

    fun lastNDaysWaterFilled(daysCount: Int): Flow<List<DayTotal>> {
        val zone = ZoneId.systemDefault()
        val startDate = LocalDate.now().minusDays((daysCount - 1).toLong())
        val startOfStartDateMillis = startDate.atStartOfDay(zone).toInstant().toEpochMilli()

        val keys = (0 until daysCount).map { i ->
            LocalDate.now().minusDays((daysCount - 1L - i)).toString()
        }

        return waterRepo.observeWaterByDay(sinceMillis = startOfStartDateMillis).map { raw ->
            val map = raw.associateBy { it.day } // yyyy-MM-dd -> DayTotal

            keys.map {
                map[it] ?: DayTotal(day = it, calories = 0)
            }
        }
    }
}
