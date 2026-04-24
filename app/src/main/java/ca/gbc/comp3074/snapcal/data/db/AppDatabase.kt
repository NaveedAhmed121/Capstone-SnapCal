package ca.gbc.comp3074.snapcal.data.db
import androidx.room.*
import ca.gbc.comp3074.snapcal.data.model.*
@Database(entities=[MealLog::class,WaterEntry::class,WorkoutEntry::class, User::class], version=4, exportSchema=false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealLogDao(): MealLogDao
    abstract fun waterDao(): WaterDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun userDao(): UserDao
}
