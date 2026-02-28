package com.dusty.presentation.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dusty.presentation.routes.*
import com.dusty.presentation.screens.admin.AdminDashboardScreen
import com.dusty.presentation.screens.auth.LoginScreen
import com.dusty.presentation.screens.auth.RegisterScreen
import com.dusty.presentation.screens.browse.BrowseScreen
import com.dusty.presentation.screens.cart.CartScreen
import com.dusty.presentation.screens.checkout.CheckoutScreen
import com.dusty.presentation.screens.detail.ListingDetailScreen
import com.dusty.presentation.screens.home.HomeScreen
import com.dusty.presentation.screens.profile.ProfileScreen
import com.dusty.presentation.screens.seller.CreateListingScreen
import com.dusty.presentation.screens.seller.SellerDashboardScreen

private data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any
)

private val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, HomeRoute),
    BottomNavItem("Browse", Icons.Filled.Search, Icons.Outlined.Search, BrowseRoute),
    BottomNavItem("Cart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, CartRoute),
    BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, ProfileRoute),
)

@Composable
fun DustyNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.let { dest ->
        dest.hasRoute<HomeRoute>() || dest.hasRoute<BrowseRoute>() ||
        dest.hasRoute<CartRoute>() || dest.hasRoute<ProfileRoute>()
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = when (item.route) {
                            is HomeRoute -> currentDestination?.hasRoute<HomeRoute>() == true
                            is BrowseRoute -> currentDestination?.hasRoute<BrowseRoute>() == true
                            is CartRoute -> currentDestination?.hasRoute<CartRoute>() == true
                            is ProfileRoute -> currentDestination?.hasRoute<ProfileRoute>() == true
                            else -> false
                        }

                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.1f else 1f,
                            animationSpec = tween(200)
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .graphicsLayer {
                                            scaleX = iconScale
                                            scaleY = iconScale
                                        }
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(HomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<HomeRoute> { HomeScreen(navController) }

            composable<BrowseRoute> { BrowseScreen(navController) }

            composable<BrowseCategoryRoute> { backStackEntry ->
                val route: BrowseCategoryRoute = backStackEntry.toRoute()
                BrowseScreen(navController, initialCategoryId = route.categoryId)
            }

            composable<ListingDetailRoute> { backStackEntry ->
                val route: ListingDetailRoute = backStackEntry.toRoute()
                ListingDetailScreen(route.listingId, navController)
            }

            composable<CartRoute> { CartScreen(navController) }
            composable<CheckoutRoute> { CheckoutScreen(navController) }
            composable<LoginRoute> { LoginScreen(navController) }
            composable<RegisterRoute> { RegisterScreen(navController) }
            composable<ProfileRoute> { ProfileScreen(navController) }
            composable<SellerDashboardRoute> { SellerDashboardScreen(navController) }

            composable<CreateListingRoute> { backStackEntry ->
                val route: CreateListingRoute = backStackEntry.toRoute()
                CreateListingScreen(route.editListingId, navController)
            }

            composable<AdminDashboardRoute> { AdminDashboardScreen(navController) }
        }
    }
}
