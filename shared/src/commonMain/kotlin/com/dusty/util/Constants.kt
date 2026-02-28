package com.dusty.util

fun Double.toPriceString(): String {
    val whole = toLong()
    val frac = ((this - whole) * 100 + 0.5).toInt().coerceIn(0, 99)
    return "$whole.${frac.toString().padStart(2, '0')}"
}

object Constants {
    const val SUPABASE_URL = "https://dpnjkwdiotpmqdbqqbme.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRwbmprd2Rpb3RwbXFkYnFxYm1lIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIyMjMxNTYsImV4cCI6MjA4Nzc5OTE1Nn0.hdgMhFJPF6UjtX3dgQxyvBoXVTDO2o_zOvp-8r7lorU"

    const val LISTING_PAGE_SIZE = 20
    const val STORAGE_BUCKET_LISTINGS = "listing-images"
}
