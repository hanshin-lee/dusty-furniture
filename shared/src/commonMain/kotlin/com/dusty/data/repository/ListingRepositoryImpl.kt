package com.dusty.data.repository

import com.dusty.data.model.Listing
import com.dusty.data.model.ListingStatus
import com.dusty.domain.repository.ListingRepository
import com.dusty.util.Constants
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage

class ListingRepositoryImpl(
    private val client: SupabaseClient
) : ListingRepository {

    override suspend fun getActiveListings(categoryId: String?, limit: Int, offset: Int): List<Listing> {
        return client.from("listings").select {
            filter {
                eq("status", "active")
                if (categoryId != null) eq("category_id", categoryId)
            }
            order("created_at", Order.DESCENDING)
            limit(limit.toLong())
            range(offset.toLong(), (offset + limit - 1).toLong())
        }.decodeList()
    }

    override suspend fun getFeaturedListings(limit: Int): List<Listing> {
        return client.from("listings").select {
            filter {
                eq("status", "active")
                eq("is_featured", true)
            }
            order("created_at", Order.DESCENDING)
            limit(limit.toLong())
        }.decodeList()
    }

    override suspend fun searchListings(query: String): List<Listing> {
        return client.from("listings").select {
            filter {
                eq("status", "active")
                textSearch("fts", query)
            }
            order("created_at", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun getListingById(id: String): Listing {
        return client.from("listings").select {
            filter { eq("id", id) }
        }.decodeSingle()
    }

    override suspend fun getListingsBySeller(sellerId: String): List<Listing> {
        return client.from("listings").select {
            filter { eq("seller_id", sellerId) }
            order("created_at", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun getListingsByStatus(status: ListingStatus): List<Listing> {
        return client.from("listings").select {
            filter { eq("status", status.name.lowercase()) }
            order("created_at", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun createListing(listing: Listing): Listing {
        return client.from("listings").insert(listing) {
            select()
        }.decodeSingle()
    }

    override suspend fun updateListing(listing: Listing): Listing {
        return client.from("listings").update(listing) {
            filter { eq("id", listing.id) }
            select()
        }.decodeSingle()
    }

    override suspend fun uploadImage(listingId: String, fileName: String, data: ByteArray): String {
        val path = "$listingId/$fileName"
        client.storage.from(Constants.STORAGE_BUCKET_LISTINGS).upload(path, data)
        return client.storage.from(Constants.STORAGE_BUCKET_LISTINGS).publicUrl(path)
    }
}
