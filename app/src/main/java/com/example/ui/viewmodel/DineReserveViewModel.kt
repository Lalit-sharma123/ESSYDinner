package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BookingEntity
import com.example.data.model.CompanyEntity
import com.example.data.model.CorporateApprovalEntity
import com.example.data.model.CorporateDepartmentEntity
import com.example.data.model.CorporateEmployeeEntity
import com.example.data.model.CustomerCrmEntity
import com.example.data.model.DiningOrderItemEntity
import com.example.data.model.DiningSessionEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.MenuAddonEntity
import com.example.data.model.MenuMediaEntity
import com.example.data.model.MenuNutritionEntity
import com.example.data.model.MenuVariantEntity
import com.example.data.model.OfferEntity
import com.example.data.model.RestaurantEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.StaffTaskEntity
import com.example.data.model.WaitlistEntity
import com.example.data.repository.DineReserveRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppRoleMode {
    CUSTOMER, RESTAURANT_OWNER, STAFF_APP, MANAGER_DASHBOARD, PLATFORM_ADMIN
}

enum class CustomerScreen {
    HOME, SEARCH, RESTAURANT_DETAIL, BOOKING_FLOW, BOOKING_CONFIRMATION, MY_BOOKINGS, OFFERS_HUB, MEMBERSHIP, WALLET, FAVORITES_REVIEWS, NOTIFICATIONS,
    WAITLIST_HUB, QR_DINING_PORTAL, DIGITAL_MENU_BUILDER, RESTAURANT_CRM, CORPORATE_DINING
}

enum class SortOption {
    DISTANCE, RATING, DISCOUNT, PRICE_LOW_TO_HIGH, PRICE_HIGH_TO_LOW
}

data class BookingDraft(
    val restaurantId: String = "",
    val restaurantName: String = "",
    val restaurantAddress: String = "",
    val guestCount: Int = 2,
    val bookingDate: String = "2026-08-01",
    val bookingTimeSlot: String = "07:30 PM",
    val occasion: String = "Casual Dining",
    val seatingPref: String = "Standard Table",
    val specialRequests: String = "",
    val selectedOfferTitle: String = "FLAT 30% OFF on Food",
    val discountPercent: Int = 30,
    val estimatedCostForTwo: Double = 80.0
)

data class FilterParams(
    val query: String,
    val categoryTag: String?,
    val cuisine: String?,
    val filters: Set<String>,
    val sort: SortOption
)

class DineReserveViewModel(private val repository: DineReserveRepository) : ViewModel() {

    // App Mode
    private val _currentMode = MutableStateFlow(AppRoleMode.CUSTOMER)
    val currentMode: StateFlow<AppRoleMode> = _currentMode.asStateFlow()

    // Customer Screen
    private val _currentScreen = MutableStateFlow(CustomerScreen.HOME)
    val currentScreen: StateFlow<CustomerScreen> = _currentScreen.asStateFlow()

    // Location
    private val _selectedCity = MutableStateFlow("New York")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    // Detail Restaurant ID
    private val _selectedRestaurantId = MutableStateFlow<String?>("rest_1")
    val selectedRestaurantId: StateFlow<String?> = _selectedRestaurantId.asStateFlow()

    // Booking Draft
    private val _bookingDraft = MutableStateFlow(BookingDraft())
    val bookingDraft: StateFlow<BookingDraft> = _bookingDraft.asStateFlow()

    // Last confirmed booking
    private val _confirmedBooking = MutableStateFlow<BookingEntity?>(null)
    val confirmedBooking: StateFlow<BookingEntity?> = _confirmedBooking.asStateFlow()

    // Search & Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryTag = MutableStateFlow<String?>(null)
    val selectedCategoryTag: StateFlow<String?> = _selectedCategoryTag.asStateFlow()

    private val _selectedCuisine = MutableStateFlow<String?>(null)
    val selectedCuisine: StateFlow<String?> = _selectedCuisine.asStateFlow()

    private val _activeFilters = MutableStateFlow<Set<String>>(emptySet())
    val activeFilters: StateFlow<Set<String>> = _activeFilters.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.RATING)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    // User Membership Tier
    private val _userMembershipTier = MutableStateFlow("Gold VIP")
    val userMembershipTier: StateFlow<String> = _userMembershipTier.asStateFlow()

    private val _rewardPoints = MutableStateFlow(850)
    val rewardPoints: StateFlow<Int> = _rewardPoints.asStateFlow()

    // Data Flows from Room
    val allRestaurants: StateFlow<List<RestaurantEntity>> = repository.allRestaurants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBookings: StateFlow<List<BookingEntity>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOffers: StateFlow<List<OfferEntity>> = repository.activeOffers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val walletTransactions = repository.walletTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- MODULE 1: WAITLIST STATE ---
    val allWaitlistEntries: StateFlow<List<WaitlistEntity>> = repository.allWaitlistEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _waitlistCountdownSeconds = MutableStateFlow(300) // 5 minutes
    val waitlistCountdownSeconds: StateFlow<Int> = _waitlistCountdownSeconds.asStateFlow()

    // --- MODULE 2: QR DINING STATE ---
    val activeDiningSession: StateFlow<DiningSessionEntity?> = repository.activeDiningSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeSessionOrders: StateFlow<List<DiningOrderItemEntity>> = repository.getOrdersForSession("ds_active")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSessionRequests: StateFlow<List<ServiceRequestEntity>> = repository.getServiceRequestsForSession("ds_active")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStaffTasks: StateFlow<List<StaffTaskEntity>> = repository.allStaffTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- MODULE 3: DIGITAL MENU STATE ---
    fun getMenuNutrition(menuItemId: String) = repository.getNutritionForMenuItem(menuItemId)
    fun getMenuVariants(menuItemId: String) = repository.getVariantsForMenuItem(menuItemId)
    fun getMenuAddons(menuItemId: String) = repository.getAddonsForMenuItem(menuItemId)

    // --- MODULE 4: CRM STATE ---
    val crmCustomerProfiles: StateFlow<List<CustomerCrmEntity>> = repository.crmCustomerProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _crmSegmentFilter = MutableStateFlow<String?>("All")
    val crmSegmentFilter: StateFlow<String?> = _crmSegmentFilter.asStateFlow()

    private val _crmSearchQuery = MutableStateFlow("")
    val crmSearchQuery: StateFlow<String> = _crmSearchQuery.asStateFlow()

    // --- MODULE 5: CORPORATE DINING STATE ---
    val corporateCompany: StateFlow<CompanyEntity?> = repository.corporateCompany
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val corporateDepartments: StateFlow<List<CorporateDepartmentEntity>> = repository.corporateDepartments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val corporateEmployees: StateFlow<List<CorporateEmployeeEntity>> = repository.corporateEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val corporateApprovals: StateFlow<List<CorporateApprovalEntity>> = repository.corporateApprovals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getMenuItemsByRestaurant(restaurantId: String): Flow<List<MenuItemEntity>> =
        repository.getMenuItemsByRestaurant(restaurantId)

    fun getOffersForRestaurant(restaurantId: String): Flow<List<OfferEntity>> =
        repository.getOffersForRestaurant(restaurantId)

    fun getReviewsForRestaurant(restaurantId: String): Flow<List<ReviewEntity>> =
        repository.getReviewsForRestaurant(restaurantId)

    // Filter Params Combination
    private val _filterParams: Flow<FilterParams> = combine(
        _searchQuery,
        _selectedCategoryTag,
        _selectedCuisine,
        _activeFilters,
        _selectedSort
    ) { query, categoryTag, cuisine, filters, sort ->
        FilterParams(query, categoryTag, cuisine, filters, sort)
    }

    // Filtered Restaurants Flow
    val filteredRestaurants: StateFlow<List<RestaurantEntity>> = combine(
        repository.allRestaurants,
        _filterParams
    ) { restaurants, params ->
        var list = restaurants
        val query = params.query
        val categoryTag = params.categoryTag
        val cuisine = params.cuisine
        val filters = params.filters
        val sort = params.sort

        if (query.isNotBlank()) {
            list = list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.cuisine.contains(query, ignoreCase = true) ||
                it.area.contains(query, ignoreCase = true)
            }
        }

        if (categoryTag != null) {
            list = list.filter { it.tagsCsv.contains(categoryTag, ignoreCase = true) }
        }

        if (cuisine != null) {
            list = list.filter { it.cuisine.contains(cuisine, ignoreCase = true) }
        }

        if (filters.isNotEmpty()) {
            if (filters.contains("Pure Veg")) list = list.filter { it.pureVeg }
            if (filters.contains("Pet Friendly")) list = list.filter { it.tagsCsv.contains("Pet Friendly", true) }
            if (filters.contains("Rooftop")) list = list.filter { it.tagsCsv.contains("Rooftop", true) }
            if (filters.contains("Fine Dining")) list = list.filter { it.tagsCsv.contains("Fine Dining", true) }
            if (filters.contains("Buffet")) list = list.filter { it.tagsCsv.contains("Buffet", true) }
            if (filters.contains("Outdoor")) list = list.filter { it.tagsCsv.contains("Outdoor", true) }
            if (filters.contains("Kid Friendly")) list = list.filter { it.kidFriendly }
            if (filters.contains("Parking")) list = list.filter { it.parkingAvailable }
        }

        when (sort) {
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.DISCOUNT -> list.sortedByDescending { it.maxDiscountPercent }
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.avgCostForTwo }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.avgCostForTwo }
            SortOption.DISTANCE -> list.sortedBy { it.name }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Actions
    fun setAppMode(mode: AppRoleMode) {
        _currentMode.value = mode
    }

    fun navigateToScreen(screen: CustomerScreen) {
        _currentScreen.value = screen
    }

    fun setCity(city: String) {
        _selectedCity.value = city
    }

    fun openRestaurantDetail(restaurantId: String) {
        _selectedRestaurantId.value = restaurantId
        _currentScreen.value = CustomerScreen.RESTAURANT_DETAIL
    }

    fun startBooking(restaurant: RestaurantEntity) {
        _selectedRestaurantId.value = restaurant.id
        _bookingDraft.value = BookingDraft(
            restaurantId = restaurant.id,
            restaurantName = restaurant.name,
            restaurantAddress = restaurant.address,
            guestCount = 2,
            bookingDate = "2026-08-01",
            bookingTimeSlot = "07:30 PM",
            occasion = "Casual Dining",
            seatingPref = "Standard Table",
            specialRequests = "",
            selectedOfferTitle = "FLAT ${restaurant.maxDiscountPercent}% OFF on Food Bill",
            discountPercent = restaurant.maxDiscountPercent,
            estimatedCostForTwo = restaurant.avgCostForTwo.toDouble()
        )
        _currentScreen.value = CustomerScreen.BOOKING_FLOW
    }

    fun updateBookingDraft(
        guests: Int? = null,
        date: String? = null,
        slot: String? = null,
        occasion: String? = null,
        seating: String? = null,
        special: String? = null,
        offerTitle: String? = null,
        discountPct: Int? = null
    ) {
        val current = _bookingDraft.value
        _bookingDraft.value = current.copy(
            guestCount = guests ?: current.guestCount,
            bookingDate = date ?: current.bookingDate,
            bookingTimeSlot = slot ?: current.bookingTimeSlot,
            occasion = occasion ?: current.occasion,
            seatingPref = seating ?: current.seatingPref,
            specialRequests = special ?: current.specialRequests,
            selectedOfferTitle = offerTitle ?: current.selectedOfferTitle,
            discountPercent = discountPct ?: current.discountPercent
        )
    }

    fun confirmBooking() {
        viewModelScope.launch {
            val draft = _bookingDraft.value
            val totalBill = draft.estimatedCostForTwo
            val discountAmt = totalBill * (draft.discountPercent / 100.0)
            val bookingCode = "#DR-${(1000..9999).random()}"
            val bookingId = "b_${UUID.randomUUID().toString().take(8)}"

            val newBooking = BookingEntity(
                id = bookingId,
                bookingCode = bookingCode,
                restaurantId = draft.restaurantId,
                restaurantName = draft.restaurantName,
                restaurantAddress = draft.restaurantAddress,
                guestCount = draft.guestCount,
                bookingDate = draft.bookingDate,
                bookingTimeSlot = draft.bookingTimeSlot,
                occasion = draft.occasion,
                seatingPref = draft.seatingPref,
                specialRequests = draft.specialRequests,
                offerTitle = draft.selectedOfferTitle,
                discountAmount = discountAmt,
                totalEstimatedBill = totalBill - discountAmt,
                status = "UPCOMING",
                qrCodePayload = "DINERESERVE-$bookingCode-${draft.restaurantId.uppercase()}"
            )

            repository.createBooking(newBooking)
            _confirmedBooking.value = newBooking
            _rewardPoints.value = _rewardPoints.value + 100
            _currentScreen.value = CustomerScreen.BOOKING_CONFIRMATION
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            repository.cancelBooking(bookingId)
        }
    }

    fun rescheduleBooking(bookingId: String, newDate: String, newSlot: String, guests: Int) {
        viewModelScope.launch {
            repository.rescheduleBooking(bookingId, newDate, newSlot, guests)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryTag(tag: String?) {
        _selectedCategoryTag.value = if (_selectedCategoryTag.value == tag) null else tag
    }

    fun toggleFilter(filterName: String) {
        val current = _activeFilters.value.toMutableSet()
        if (current.contains(filterName)) {
            current.remove(filterName)
        } else {
            current.add(filterName)
        }
        _activeFilters.value = current
    }

    fun clearFilters() {
        _searchQuery.value = ""
        _selectedCategoryTag.value = null
        _selectedCuisine.value = null
        _activeFilters.value = emptySet()
    }

    fun setSortOption(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun toggleFavorite(restaurantId: String, currentFavList: List<com.example.data.model.FavoriteEntity>) {
        viewModelScope.launch {
            val isFav = currentFavList.any { it.restaurantId == restaurantId }
            repository.toggleFavorite(restaurantId, !isFav)
        }
    }

    fun submitReview(restaurantId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            val review = ReviewEntity(
                id = UUID.randomUUID().toString(),
                restaurantId = restaurantId,
                userName = "You (Verified Diner)",
                userAvatar = "",
                rating = rating,
                foodRating = rating,
                serviceRating = rating,
                ambienceRating = rating,
                valueRating = rating,
                comment = comment,
                ownerReply = "",
                dateString = "Just Now"
            )
            repository.addReview(review)
            _rewardPoints.value = _rewardPoints.value + 50
        }
    }

    fun addOwnerReply(reviewId: String, replyText: String) {
        viewModelScope.launch {
            repository.addOwnerReply(reviewId, replyText)
        }
    }

    fun addMenuItem(restaurantId: String, name: String, desc: String, category: String, price: Double, isVeg: Boolean) {
        viewModelScope.launch {
            val item = MenuItemEntity(
                id = "m_${UUID.randomUUID().toString().take(6)}",
                restaurantId = restaurantId,
                name = name,
                description = desc,
                category = category,
                price = price,
                isVeg = isVeg,
                isChefSpecial = false,
                isRecommended = true
            )
            repository.insertMenuItem(item)
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            repository.deleteMenuItem(itemId)
        }
    }

    fun createOffer(restaurantId: String, title: String, type: String, discountPct: Int, maxDiscount: Double) {
        viewModelScope.launch {
            val offer = OfferEntity(
                id = "off_${UUID.randomUUID().toString().take(6)}",
                restaurantId = restaurantId,
                title = title,
                offerType = type,
                discountPercent = discountPct,
                maxDiscountAmount = maxDiscount,
                minBillAmount = 40.0,
                validDays = "Mon-Sun",
                validTimes = "12:00 PM - 11:00 PM",
                terms = "Valid on DineReserve bookings."
            )
            repository.createOffer(offer)
        }
    }

    fun addWalletFunds(amount: Double) {
        viewModelScope.launch {
            repository.addWalletFunds(amount)
        }
    }

    fun upgradeMembership(tierName: String) {
        _userMembershipTier.value = tierName
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    // --- MODULE 1 ACTIONS: WAITLIST ---
    fun joinWaitlist(restaurantId: String, restaurantName: String, guestCount: Int, date: String, timeSlot: String) {
        viewModelScope.launch {
            val entry = WaitlistEntity(
                id = "wl_${UUID.randomUUID().toString().take(6)}",
                restaurantId = restaurantId,
                restaurantName = restaurantName,
                userId = "usr_current",
                userName = "Current User",
                guestCount = guestCount,
                requestedDate = date,
                requestedTime = timeSlot,
                priorityLevel = if (_userMembershipTier.value.contains("VIP")) "VIP" else "STANDARD",
                status = "NOTIFIED",
                expiresAtTimestamp = System.currentTimeMillis() + 300_000L,
                notifiedAtTimestamp = System.currentTimeMillis(),
                queuePosition = 1
            )
            repository.joinWaitlist(entry)
            startWaitlistTimer()
        }
    }

    private fun startWaitlistTimer() {
        _waitlistCountdownSeconds.value = 300
        viewModelScope.launch {
            while (_waitlistCountdownSeconds.value > 0) {
                delay(1000L)
                _waitlistCountdownSeconds.value -= 1
            }
        }
    }

    fun acceptWaitlistOffer(waitlistId: String) {
        viewModelScope.launch {
            val booking = repository.acceptWaitlistOffer(waitlistId)
            _confirmedBooking.value = booking
            _currentScreen.value = CustomerScreen.BOOKING_CONFIRMATION
        }
    }

    fun cancelWaitlist(waitlistId: String) {
        viewModelScope.launch {
            repository.cancelWaitlist(waitlistId)
        }
    }

    // --- MODULE 2 ACTIONS: QR DINING ---
    fun openQrDiningPortal(bookingId: String, restaurantId: String, restaurantName: String, tableNumber: String) {
        viewModelScope.launch {
            repository.startDiningSession(bookingId, restaurantId, restaurantName, tableNumber)
            _currentScreen.value = CustomerScreen.QR_DINING_PORTAL
        }
    }

    fun placeDiningOrder(menuItemId: String, itemName: String, price: Double, quantity: Int, notes: String) {
        viewModelScope.launch {
            repository.placeDiningOrder("ds_active", menuItemId, itemName, price, quantity, notes)
        }
    }

    fun requestTableService(requestType: String, note: String) {
        viewModelScope.launch {
            repository.requestService("ds_active", "Table 14", requestType, note)
        }
    }

    fun sendServiceRequest(
        requestType: String,
        note: String,
        itemsSummary: String = "",
        quantity: Int = 1,
        priority: String = "NORMAL"
    ) {
        viewModelScope.launch {
            repository.requestService(
                sessionId = "ds_active",
                tableNumber = "Table 14 (Window)",
                requestType = requestType,
                note = note,
                itemsSummary = itemsSummary,
                quantity = quantity,
                priority = priority
            )
        }
    }

    fun updateStaffTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateStaffTaskStatus(taskId, newStatus)
        }
    }

    fun assignStaffTask(taskId: String, staffId: String, staffName: String) {
        viewModelScope.launch {
            repository.assignStaffTask(taskId, staffId, staffName)
        }
    }

    fun checkoutDiningSession() {
        viewModelScope.launch {
            repository.checkoutDiningSession("ds_active")
            _currentScreen.value = CustomerScreen.HOME
        }
    }

    // --- MODULE 3 ACTIONS: DIGITAL MENU ---
    fun saveMenuNutrition(menuItemId: String, calories: Int, protein: Double, carbs: Double, fat: Double, spicy: Int, prepTime: Int, allergens: String) {
        viewModelScope.launch {
            repository.saveMenuNutrition(
                MenuNutritionEntity(
                    menuItemId = menuItemId,
                    calories = calories,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatGrams = fat,
                    spicyLevel = spicy,
                    prepTimeMinutes = prepTime,
                    allergenTagsCsv = allergens
                )
            )
        }
    }

    fun addMenuVariant(menuItemId: String, variantName: String, priceAdjustment: Double) {
        viewModelScope.launch {
            repository.addMenuVariant(
                MenuVariantEntity(
                    id = "var_${UUID.randomUUID().toString().take(6)}",
                    menuItemId = menuItemId,
                    variantName = variantName,
                    priceAdjustment = priceAdjustment
                )
            )
        }
    }

    fun addMenuAddon(menuItemId: String, addonName: String, price: Double) {
        viewModelScope.launch {
            repository.addMenuAddon(
                MenuAddonEntity(
                    id = "add_${UUID.randomUUID().toString().take(6)}",
                    menuItemId = menuItemId,
                    addonName = addonName,
                    price = price
                )
            )
        }
    }

    // --- MODULE 4 ACTIONS: CRM ---
    fun setCrmSegmentFilter(segmentTag: String?) {
        _crmSegmentFilter.value = segmentTag
    }

    fun setCrmSearchQuery(query: String) {
        _crmSearchQuery.value = query
    }

    fun updateCrmNotes(userId: String, notes: String) {
        viewModelScope.launch {
            repository.updateCrmNotes(userId, notes)
        }
    }

    // --- MODULE 5 ACTIONS: CORPORATE DINING ---
    fun addCorporateWalletFunds(amount: Double) {
        viewModelScope.launch {
            repository.addCorporateFunds("comp_1", amount)
        }
    }

    fun requestCorporateApproval(employeeName: String, amount: Double, restaurantName: String, bookingDate: String) {
        viewModelScope.launch {
            val approval = CorporateApprovalEntity(
                id = "app_${UUID.randomUUID().toString().take(6)}",
                companyId = "comp_1",
                employeeName = employeeName,
                amount = amount,
                restaurantName = restaurantName,
                bookingDate = bookingDate,
                status = "PENDING"
            )
            repository.createCorporateApproval(approval)
        }
    }

    fun updateCorporateApprovalStatus(approvalId: String, status: String) {
        viewModelScope.launch {
            repository.updateCorporateApprovalStatus(approvalId, status)
        }
    }

    // --- STAFF & SERVICE REQUEST ACTIONS ---
    fun updateTaskStatus(taskId: String, status: String) {
        viewModelScope.launch {
            repository.updateStaffTaskStatus(taskId, status)
        }
    }
}

