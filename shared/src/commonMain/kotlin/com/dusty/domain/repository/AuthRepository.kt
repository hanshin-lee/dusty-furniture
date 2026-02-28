package com.dusty.domain.repository

import com.dusty.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    val currentUserId: String?
    suspend fun signUp(email: String, password: String, displayName: String)
    suspend fun signIn(email: String, password: String)
    suspend fun signOut()
    suspend fun getCurrentProfile(): Profile?
}
