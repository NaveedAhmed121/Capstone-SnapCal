package ca.gbc.comp3074.snapcal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val createdAt: Long = System.currentTimeMillis(),
)
