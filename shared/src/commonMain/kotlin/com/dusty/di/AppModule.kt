package com.dusty.di

import com.dusty.data.remote.SupabaseProvider
import com.dusty.data.repository.*
import com.dusty.domain.repository.*
import com.dusty.presentation.screens.admin.AdminDashboardViewModel
import com.dusty.presentation.screens.auth.LoginViewModel
import com.dusty.presentation.screens.auth.RegisterViewModel
import com.dusty.presentation.screens.browse.BrowseViewModel
import com.dusty.presentation.screens.cart.CartViewModel
import com.dusty.presentation.screens.checkout.CheckoutViewModel
import com.dusty.presentation.screens.detail.ListingDetailViewModel
import com.dusty.presentation.screens.home.HomeViewModel
import com.dusty.presentation.screens.profile.ProfileViewModel
import com.dusty.presentation.screens.seller.CreateListingViewModel
import com.dusty.presentation.screens.seller.SellerDashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Supabase client
    single { SupabaseProvider.client }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<ListingRepository> { ListingRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<CartRepository> { CartRepositoryImpl(get()) }
    single<OrderRepository> { OrderRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { BrowseViewModel(get(), get()) }
    viewModel { params -> ListingDetailViewModel(params.get(), get(), get(), get()) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { CheckoutViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { SellerDashboardViewModel(get(), get()) }
    viewModel { params -> CreateListingViewModel(params.getOrNull(), get(), get(), get()) }
    viewModel { AdminDashboardViewModel(get(), get(), get()) }
}
