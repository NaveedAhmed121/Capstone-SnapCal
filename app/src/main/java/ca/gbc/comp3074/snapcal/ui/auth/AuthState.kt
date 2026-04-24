package ca.gbc.comp3074.snapcal.ui.auth

import androidx.compose.runtime.mutableStateOf
import ca.gbc.comp3074.snapcal.data.model.User

object AuthState {
    val isLoggedIn = mutableStateOf(false)
    val currentUser = mutableStateOf<User?>(null)
}
