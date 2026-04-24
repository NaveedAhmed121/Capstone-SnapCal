package ca.gbc.comp3074.snapcal.data.repo

import ca.gbc.comp3074.snapcal.data.db.UserDao
import ca.gbc.comp3074.snapcal.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun signup(user: User) = userDao.insert(user)
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    suspend fun deleteUser(user: User) = userDao.delete(user)
}
