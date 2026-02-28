package com.dusty.domain.repository

import com.dusty.data.model.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
    suspend fun getCategoryById(id: String): Category
    suspend fun createCategory(category: Category): Category
    suspend fun updateCategory(category: Category): Category
    suspend fun deleteCategory(id: String)
}
