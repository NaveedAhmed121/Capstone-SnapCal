package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.model.User
import ca.gbc.comp3074.snapcal.data.repo.UserRepository
import ca.gbc.comp3074.snapcal.ui.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: UserRepository) : ViewModel() {

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val user = repo.getUserByEmail(email)
            if (user != null && user.passwordHash == pass) { // In a real app, use BCrypt
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
            try {
                val newUser = User(username = username, email = email, passwordHash = pass, isAdmin = email.contains("admin"))
                repo.signup(newUser)
                AuthState.isLoggedIn.value = true
                AuthState.currentUser.value = newUser
                onSuccess()
            } catch (e: Exception) {
                _error.value = "User already exists or error occurred"
            }
        }
    }
}

class AuthViewModelFactory(private val repo: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}
