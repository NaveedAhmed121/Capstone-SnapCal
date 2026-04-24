package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.model.User
import ca.gbc.comp3074.snapcal.data.repo.UserRepository
import ca.gbc.comp3074.snapcal.ui.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest

class AuthViewModel(private val repo: UserRepository) : ViewModel() {

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val hashedPassword = hashPassword(pass)
            val user = repo.getUserByEmail(email)
            if (user != null && user.passwordHash == hashedPassword) {
                AuthState.isLoggedIn.value = true
                AuthState.currentUser.value = user
                onSuccess()
            } else {
                _error.value = "Invalid email or password"
            }
        }
    }

    fun signup(username: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val existing = repo.getUserByEmail(email)
            if (existing != null) {
                _error.value = "User already exists"
                return@launch
            }
            
            val hashedPassword = hashPassword(pass)
            // Properly set isAdmin role - only for specific emails or admin seed
            val isAdmin = email.lowercase().startsWith("admin@")
            
            val newUser = User(
                username = username,
                email = email,
                passwordHash = hashedPassword,
                isAdmin = isAdmin
            )
            repo.insert(newUser)
            AuthState.isLoggedIn.value = true
            AuthState.currentUser.value = newUser
            onSuccess()
        }
    }

    fun logout() {
        AuthState.isLoggedIn.value = false
        AuthState.currentUser.value = null
    }
}
