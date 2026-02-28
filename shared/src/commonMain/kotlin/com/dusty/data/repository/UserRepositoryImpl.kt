package com.dusty.data.repository

import com.dusty.data.model.Profile
import com.dusty.data.model.UserRole
import com.dusty.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class UserRepositoryImpl(
    private val client: SupabaseClient
) : UserRepository {

    override suspend fun getProfile(userId: String): Profile {
        return client.from("profiles").select {
            filter { eq("id", userId) }
        }.decodeSingle()
    }

    override suspend fun updateProfile(profile: Profile): Profile {
        return client.from("profiles").update(profile) {
            filter { eq("id", profile.id) }
            select()
        }.decodeSingle()
    }

    override suspend fun requestSellerRole(userId: String) {
        client.from("profiles").update({
            set("role", "seller")
        }) {
            filter { eq("id", userId) }
        }
    }

    override suspend fun getAllUsers(): List<Profile> {
        return client.from("profiles").select().decodeList()
    }

    override suspend fun setUserRole(userId: String, role: UserRole) {
        client.from("profiles").update({
            set("role", role.name.lowercase())
        }) {
            filter { eq("id", userId) }
        }
    }

    override suspend fun setSellerVerified(userId: String, verified: Boolean) {
        client.from("profiles").update({
            set("seller_verified", verified)
        }) {
            filter { eq("id", userId) }
        }
    }
}
