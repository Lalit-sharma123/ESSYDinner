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
import com.example.ui.screens.CorporateDiningScreen
import com.example.ui.screens.DigitalMenuBuilderScreen
import com.example.ui.screens.FavoritesReviewsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MembershipScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OffersHubScreen
import com.example.ui.screens.OwnerPanelScreen
import com.example.ui.screens.QrDiningPortalScreen
import com.example.ui.screens.RestaurantCrmScreen
import com.example.ui.screens.RestaurantDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WaitlistScreen
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
            notificationDao = database.notificationDao(),
            waitlistDao = database.waitlistDao(),
            diningSessionDao = database.diningSessionDao(),
            diningOrderDao = database.diningOrderDao(),
            serviceRequestDao = database.serviceRequestDao(),
            digitalMenuDao = database.digitalMenuDao(),
            crmDao = database.crmDao(),
            corporateDao = database.corporateDao()
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

    // Module 1, 2, 3, 4, 5 State Collections
    val allWaitlistEntries by viewModel.allWaitlistEntries.collectAsStateWithLifecycle()
    val waitlistCountdownSeconds by viewModel.waitlistCountdownSeconds.collectAsStateWithLifecycle()

    val activeDiningSession by viewModel.activeDiningSession.collectAsStateWithLifecycle()
    val activeSessionOrders by viewModel.activeSessionOrders.collectAsStateWithLifecycle()
    val activeSessionRequests by viewModel.activeSessionRequests.collectAsStateWithLifecycle()

    val crmCustomerProfiles by viewModel.crmCustomerProfiles.collectAsStateWithLifecycle()
    val crmSegmentFilter by viewModel.crmSegmentFilter.collectAsStateWithLifecycle()
    val crmSearchQuery by viewModel.crmSearchQuery.collectAsStateWithLifecycle()

    val corporateCompany by viewModel.corporateCompany.collectAsStateWithLifecycle()
    val corporateDepartments by viewModel.corporateDepartments.collectAsStateWithLifecycle()
    val corporateEmployees by viewModel.corporateEmployees.collectAsStateWithLifecycle()
    val corporateApprovals by viewModel.corporateApprovals.collectAsStateWithLifecycle()

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
                            onMembershipClick = { viewModel.navigateToScreen(CustomerScreen.MEMBERSHIP) },
                            onWaitlistClick = { viewModel.navigateToScreen(CustomerScreen.WAITLIST_HUB) },
                            onQrDiningClick = { viewModel.navigateToScreen(CustomerScreen.QR_DINING_PORTAL) },
                            onCorporateClick = { viewModel.navigateToScreen(CustomerScreen.CORPORATE_DINING) },
                            onCrmClick = { viewModel.navigateToScreen(CustomerScreen.RESTAURANT_CRM) },
                            onMenuBuilderClick = { viewModel.navigateToScreen(CustomerScreen.DIGITAL_MENU_BUILDER) }
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
                                    },
                                    onJoinWaitlist = { restId, restName, partySize, date, timeSlot ->
                                        viewModel.joinWaitlist(restId, restName, partySize, date, timeSlot)
                                        viewModel.navigateToScreen(CustomerScreen.WAITLIST_HUB)
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
                        CustomerScreen.WAITLIST_HUB -> WaitlistScreen(
                            waitlistEntries = allWaitlistEntries,
                            countdownSeconds = waitlistCountdownSeconds,
                            onAcceptOffer = { viewModel.acceptWaitlistOffer(it) },
                            onCancelWaitlist = { viewModel.cancelWaitlist(it) },
                            onBackClick = { viewModel.navigateToScreen(CustomerScreen.HOME) }
                        )
                        CustomerScreen.QR_DINING_PORTAL -> QrDiningPortalScreen(
                            session = activeDiningSession,
                            menuItems = detailMenuItems,
                            placedOrders = activeSessionOrders,
                            serviceRequests = activeSessionRequests,
                            onPlaceOrder = { id, name, price, qty, notes ->
                                viewModel.placeDiningOrder(id, name, price, qty, notes)
                            },
                            onRequestService = { type, note ->
                                viewModel.requestTableService(type, note)
                            },
                            onCheckout = { viewModel.checkoutDiningSession() },
                            onBackClick = { viewModel.navigateToScreen(CustomerScreen.HOME) }
                        )
                        CustomerScreen.DIGITAL_MENU_BUILDER -> DigitalMenuBuilderScreen(
                            menuItems = detailMenuItems,
                            onSaveNutrition = { id, cal, pro, carb, fat, spicy, prep, alg ->
                                viewModel.saveMenuNutrition(id, cal, pro, carb, fat, spicy, prep, alg)
                            },
                            onAddVariant = { id, name, adj ->
                                viewModel.addMenuVariant(id, name, adj)
                            },
                            onAddAddon = { id, name, price ->
                                viewModel.addMenuAddon(id, name, price)
                            },
                            onBackClick = { viewModel.navigateToScreen(CustomerScreen.HOME) }
                        )
                        CustomerScreen.RESTAURANT_CRM -> RestaurantCrmScreen(
                            customerProfiles = crmCustomerProfiles,
                            selectedSegment = crmSegmentFilter,
                            searchQuery = crmSearchQuery,
                            onSegmentSelected = { viewModel.setCrmSegmentFilter(it) },
                            onSearchQueryChange = { viewModel.setCrmSearchQuery(it) },
                            onUpdateNotes = { userId, notes -> viewModel.updateCrmNotes(userId, notes) },
                            onBackClick = { viewModel.navigateToScreen(CustomerScreen.HOME) }
                        )
                        CustomerScreen.CORPORATE_DINING -> CorporateDiningScreen(
                            company = corporateCompany,
                            departments = corporateDepartments,
                            employees = corporateEmployees,
                            approvals = corporateApprovals,
                            onAddFunds = { viewModel.addCorporateWalletFunds(it) },
                            onRequestApproval = { emp, amt, rest, dt ->
                                viewModel.requestCorporateApproval(emp, amt, rest, dt)
                            },
                            onUpdateApprovalStatus = { id, status ->
                                viewModel.updateCorporateApprovalStatus(id, status)
                            },
                            onBackClick = { viewModel.navigateToScreen(CustomerScreen.HOME) }
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
                            },
                            onOpenMenuBuilder = { viewModel.navigateToScreen(CustomerScreen.DIGITAL_MENU_BUILDER) },
                            onOpenCrm = { viewModel.navigateToScreen(CustomerScreen.RESTAURANT_CRM) }
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
