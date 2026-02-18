package ca.gbc.comp3074.snapcal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ca.gbc.comp3074.snapcal.data.model.MealLog
import ca.gbc.comp3074.snapcal.data.model.WaterEntry

@Database(
    entities = [MealLog::class, WaterEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealLogDao(): MealLogDao
    abstract fun waterDao(): WaterDao
}
