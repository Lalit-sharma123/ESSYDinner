package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.DineReserveDatabase
import com.example.data.repository.DineReserveRepository
import com.example.ui.components.CustomerBottomNavigation
import com.example.ui.components.TopRoleHeaderBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.BookingConfirmationScreen
import com.example.ui.screens.BookingModalScreen
import com.example.ui.screens.FavoritesReviewsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MembershipScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OffersHubScreen
import com.example.ui.screens.OwnerPanelScreen
import com.example.ui.screens.RestaurantDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.DineReserveTheme
import com.example.ui.viewmodel.AppRoleMode
import com.example.ui.viewmodel.CustomerScreen
import com.example.ui.viewmodel.DineReserveViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: DineReserveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init Room Database and Repository
        val database = DineReserveDatabase.getDatabase(this)
        val repository = DineReserveRepository(
            restaurantDao = database.restaurantDao(),
            menuItemDao = database.menuItemDao(),
            bookingDao = database.bookingDao(),
            offerDao = database.offerDao(),
            walletDao = database.walletDao(),
            reviewDao = database.reviewDao(),
            favoriteDao = database.favoriteDao(),
            notificationDao = database.notificationDao()
        )

        viewModel = DineReserveViewModel(repository)

        setContent {
            DineReserveTheme {
                DineReserveAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DineReserveAppContent(viewModel: DineReserveViewModel) {
    val currentMode by viewModel.currentMode.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val selectedRestaurantId by viewModel.selectedRestaurantId.collectAsStateWithLifecycle()

    val allRestaurants by viewModel.allRestaurants.collectAsStateWithLifecycle()
    val filteredRestaurants by viewModel.filteredRestaurants.collectAsStateWithLifecycle()
    val activeOffers by viewModel.activeOffers.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val bookingDraft by viewModel.bookingDraft.collectAsStateWithLifecycle()
    val confirmedBooking by viewModel.confirmedBooking.collectAsStateWithLifecycle()
    val walletTransactions by viewModel.walletTransactions.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryTag by viewModel.selectedCategoryTag.collectAsStateWithLifecycle()
    val activeFilters by viewModel.activeFilters.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()

    val membershipTier by viewModel.userMembershipTier.collectAsStateWithLifecycle()
    val rewardPoints by viewModel.rewardPoints.collectAsStateWithLifecycle()

    val unreadNotifications = notifications.count { !it.isRead }

    val currentDetailRestaurant = allRestaurants.find { it.id == selectedRestaurantId } ?: allRestaurants.firstOrNull()

    val detailMenuItems by (if (currentDetailRestaurant != null) {
        viewModel.getMenuItemsByRestaurant(currentDetailRestaurant.id).collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    })

    val detailOffers by (if (currentDetailRestaurant != null) {
        viewModel.getOffersForRestaurant(currentDetailRestaurant.id).collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    })

    val detailReviews by (if (currentDetailRestaurant != null) {
        viewModel.getReviewsForRestaurant(currentDetailRestaurant.id).collectAsStateWithLifecycle(initialValue = emptyList())
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    })

    Scaffold(
        topBar = {
            TopRoleHeaderBar(
                currentMode = currentMode,
                selectedCity = selectedCity,
                unreadNotificationCount = unreadNotifications,
                onModeChange = { viewModel.setAppMode(it) },
                onCityChange = { viewModel.setCity(it) },
                onOpenNotifications = {
                    viewModel.setAppMode(AppRoleMode.CUSTOMER)
                    viewModel.navigateToScreen(CustomerScreen.NOTIFICATIONS)
                }
            )
        },
        bottomBar = {
            if (currentMode == AppRoleMode.CUSTOMER &&
                currentScreen in listOf(
                    CustomerScreen.HOME, CustomerScreen.SEARCH, CustomerScreen.OFFERS_HUB, CustomerScreen.MY_BOOKINGS, CustomerScreen.WALLET
                )
            ) {
                CustomerBottomNavigation(
                    currentScreen = currentScreen,
                    onScreenSelect = { viewModel.navigateToScreen(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentMode) {
                AppRoleMode.CUSTOMER -> {
                    when (currentScreen) {
                        CustomerScreen.HOME -> HomeScreen(
                            restaurants = filteredRestaurants,
                            offers = activeOffers,
                            favorites = favorites,
                            membershipTier = membershipTier,
                            rewardPoints = rewardPoints,
                            selectedCategoryTag = selectedCategoryTag,
                            onCategoryTagSelect = { viewModel.setCategoryTag(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it, favorites) },
                            onRestaurantClick = { viewModel.openRestaurantDetail(it) },
                            onBookClick = { viewModel.startBooking(it) },
                            onSearchClick = { viewModel.navigateToScreen(CustomerScreen.SEARCH) },
                            onOffersHubClick = { viewModel.navigateToScreen(CustomerScreen.OFFERS_HUB) },
                            onMembershipClick = { viewModel.navigateToScreen(CustomerScreen.MEMBERSHIP) }
                        )
                        CustomerScreen.SEARCH -> SearchScreen(
                            searchQuery = searchQuery,
                            activeFilters = activeFilters,
                            selectedSort = selectedSort,
                            restaurants = filteredRestaurants,
                            favorites = favorites,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onToggleFilter = { viewModel.toggleFilter(it) },
                            onSortChange = { viewModel.setSortOption(it) },
                            onClearFilters = { viewModel.clearFilters() },
                            onToggleFavorite = { viewModel.toggleFavorite(it, favorites) },
                            onRestaurantClick = { viewModel.openRestaurantDetail(it) },
                            onBookClick = { viewModel.startBooking(it) }
                        )
                        CustomerScreen.RESTAURANT_DETAIL -> {
                            if (currentDetailRestaurant != null) {
                                RestaurantDetailScreen(
                                    restaurant = currentDetailRestaurant,
                                    menuItems = detailMenuItems,
                                    offers = detailOffers,
                                    reviews = detailReviews,
                                    favorites = favorites,
                                    onBackClick = { viewModel.navigateToScreen(CustomerScreen.HOME) },
                                    onToggleFavorite = { viewModel.toggleFavorite(it, favorites) },
                                    onBookClick = { viewModel.startBooking(it) },
                                    onSubmitReview = { restId, rating, comment ->
                                        viewModel.submitReview(restId, rating, comment)
                                    }
                                )
                            }
                        }
                        CustomerScreen.BOOKING_FLOW -> BookingModalScreen(
                            draft = bookingDraft,
                            onDraftUpdate = { g, d, s, o, seat, sp ->
                                viewModel.updateBookingDraft(g, d, s, o, seat, sp)
                            },
                            onConfirmClick = { viewModel.confirmBooking() },
                            onBackClick = { viewModel.navigateToScreen(CustomerScreen.RESTAURANT_DETAIL) }
                        )
                        CustomerScreen.BOOKING_CONFIRMATION -> BookingConfirmationScreen(
                            booking = confirmedBooking,
                            onViewBookingsClick = { viewModel.navigateToScreen(CustomerScreen.MY_BOOKINGS) },
                            onBackHomeClick = { viewModel.navigateToScreen(CustomerScreen.HOME) }
                        )
                        CustomerScreen.MY_BOOKINGS -> MyBookingsScreen(
                            bookings = allBookings,
                            onCancelBooking = { viewModel.cancelBooking(it) },
                            onRescheduleBooking = { id, date, slot, guests ->
                                viewModel.rescheduleBooking(id, date, slot, guests)
                            }
                        )
                        CustomerScreen.OFFERS_HUB -> OffersHubScreen(
                            offers = activeOffers,
                            restaurants = allRestaurants,
                            onBookWithOffer = { viewModel.startBooking(it) }
                        )
                        CustomerScreen.MEMBERSHIP -> MembershipScreen(
                            currentTier = membershipTier,
                            rewardPoints = rewardPoints,
                            onUpgradeTier = { viewModel.upgradeMembership(it) }
                        )
                        CustomerScreen.WALLET -> WalletScreen(
                            transactions = walletTransactions,
                            rewardPoints = rewardPoints,
                            onAddFunds = { viewModel.addWalletFunds(it) }
                        )
                        CustomerScreen.FAVORITES_REVIEWS -> FavoritesReviewsScreen(
                            favorites = favorites,
                            restaurants = allRestaurants,
                            onToggleFavorite = { viewModel.toggleFavorite(it, favorites) },
                            onRestaurantClick = { viewModel.openRestaurantDetail(it) },
                            onBookClick = { viewModel.startBooking(it) }
                        )
                        CustomerScreen.NOTIFICATIONS -> NotificationsScreen(
                            notifications = notifications,
                            onMarkAsRead = { viewModel.markNotificationAsRead(it) }
                        )
                    }
                }
                AppRoleMode.RESTAURANT_OWNER -> {
                    if (currentDetailRestaurant != null) {
                        OwnerPanelScreen(
                            restaurant = currentDetailRestaurant,
                            menuItems = detailMenuItems,
                            bookings = allBookings,
                            reviews = detailReviews,
                            onAddMenuItem = { name, desc, cat, price, isVeg ->
                                viewModel.addMenuItem(currentDetailRestaurant.id, name, desc, cat, price, isVeg)
                            },
                            onDeleteMenuItem = { viewModel.deleteMenuItem(it) },
                            onCreateOffer = { title, type, pct, maxDisc ->
                                viewModel.createOffer(currentDetailRestaurant.id, title, type, pct, maxDisc)
                            },
                            onOwnerReply = { reviewId, reply ->
                                viewModel.addOwnerReply(reviewId, reply)
                            }
                        )
                    }
                }
                AppRoleMode.PLATFORM_ADMIN -> {
                    AdminPanelScreen(
                        restaurants = allRestaurants
                    )
                }
            }
        }
    }
}
