package ca.gbc.comp3074.snapcal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val isAdmin: Boolean = false
)
