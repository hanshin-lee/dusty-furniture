package com.dusty.domain.repository

import com.dusty.data.model.Profile
import com.dusty.data.model.UserRole

interface UserRepository {
    suspend fun getProfile(userId: String): Profile
    suspend fun updateProfile(profile: Profile): Profile
    suspend fun requestSellerRole(userId: String)
    suspend fun getAllUsers(): List<Profile>
    suspend fun setUserRole(userId: String, role: UserRole)
    suspend fun setSellerVerified(userId: String, verified: Boolean)
}
