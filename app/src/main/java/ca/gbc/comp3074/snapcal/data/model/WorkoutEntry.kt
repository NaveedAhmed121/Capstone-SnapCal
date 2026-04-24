package ca.gbc.comp3074.snapcal.data.model
import androidx.room.Entity; import androidx.room.PrimaryKey
@Entity(tableName = "workout_entries")
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, val caloriesBurned: Int, val dateMillis: Long,
    val isScheduled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
