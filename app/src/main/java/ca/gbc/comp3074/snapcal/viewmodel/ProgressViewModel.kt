package ca.gbc.comp3074.snapcal.viewmodel
import androidx.lifecycle.*
import ca.gbc.comp3074.snapcal.data.db.DayTotal
import ca.gbc.comp3074.snapcal.data.repo.*
import kotlinx.coroutines.flow.*
import java.time.*

class ProgressViewModel(
    private val mealRepo: MealRepository,
    private val waterRepo: WaterRepository,
    private val workoutRepo: WorkoutRepository
) : ViewModel() {

    private fun startMillis(n: Int): Long {
        val zone = ZoneId.systemDefault()
        return LocalDate.now().minusDays((n - 1).toLong()).atStartOfDay(zone).toInstant().toEpochMilli()
    }

    private fun keys(n: Int) = (0 until n).map { LocalDate.now().minusDays((n - 1L - it)).toString() }

    private fun fill(raw: List<DayTotal>, n: Int): List<DayTotal> {
        val map = raw.associateBy { it.day }
        return keys(n).map { map[it] ?: DayTotal(day = it, calories = 0) }
    }

    fun lastNDaysCaloriesFilled(n: Int) = mealRepo.observeCaloriesByDay(startMillis(n)).map { fill(it, n) }
    fun lastNDaysWaterFilled(n: Int) = waterRepo.observeWaterByDay(startMillis(n)).map { fill(it, n) }
    fun lastNDaysBurnedFilled(n: Int) = workoutRepo.observeBurnedByDay(startMillis(n)).map { fill(it, n) }

    fun getAverageCalories(n: Int): Flow<Int> = lastNDaysCaloriesFilled(n).map { list ->
        if (list.isEmpty()) 0 else list.sumOf { it.calories } / n
    }

    fun getAverageBurned(n: Int): Flow<Int> = lastNDaysBurnedFilled(n).map { list ->
        if (list.isEmpty()) 0 else list.sumOf { it.calories } / n
    }
}

class ProgressViewModelFactory(
    private val mealRepo: MealRepository,
    private val waterRepo: WaterRepository,
    private val workoutRepo: WorkoutRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProgressViewModel(mealRepo, waterRepo, workoutRepo) as T
    }
}
