package com.dusty.data.repository

import com.dusty.data.model.Category
import com.dusty.domain.repository.CategoryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class CategoryRepositoryImpl(
    private val client: SupabaseClient
) : CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return client.from("categories").select {
            order("display_order", Order.ASCENDING)
        }.decodeList()
    }

    override suspend fun getCategoryById(id: String): Category {
        return client.from("categories").select {
            filter { eq("id", id) }
        }.decodeSingle()
    }

    override suspend fun createCategory(category: Category): Category {
        return client.from("categories").insert(category) {
            select()
        }.decodeSingle()
    }

    override suspend fun updateCategory(category: Category): Category {
        return client.from("categories").update(category) {
            filter { eq("id", category.id) }
            select()
        }.decodeSingle()
    }

    override suspend fun deleteCategory(id: String) {
        client.from("categories").delete {
            filter { eq("id", id) }
        }
    }
}
