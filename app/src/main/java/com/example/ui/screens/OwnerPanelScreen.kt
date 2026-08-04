package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BookingEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.RestaurantEntity
import com.example.data.model.ReviewEntity
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldOnPrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.VegGreen

@Composable
fun OwnerPanelScreen(
    restaurant: RestaurantEntity,
    menuItems: List<MenuItemEntity>,
    bookings: List<BookingEntity>,
    reviews: List<ReviewEntity>,
    onAddMenuItem: (name: String, desc: String, category: String, price: Double, isVeg: Boolean) -> Unit,
    onDeleteMenuItem: (String) -> Unit,
    onCreateOffer: (title: String, type: String, discountPct: Int, maxDiscount: Double) -> Unit,
    onOwnerReply: (reviewId: String, reply: String) -> Unit,
    onOpenMenuBuilder: () -> Unit = {},
    onOpenCrm: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Operations", "Table Mgmt (Live Floor)", "Reservations", "Menu Manager", "Offer Rules", "Analytics")
    var showVipAlert by remember { mutableStateOf(true) }

    // Form states
    var newDishName by remember { mutableStateOf("") }
    var newDishDesc by remember { mutableStateOf("") }
    var newDishPrice by remember { mutableStateOf("18.0") }
    var newDishCategory by remember { mutableStateOf("Main Course") }
    var newDishIsVeg by remember { mutableStateOf(true) }

    var offerTitleInput by remember { mutableStateOf("HAPPY HOURS 40% OFF") }
    var offerPctInput by remember { mutableStateOf("40") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Owner Header Banner
        Surface(color = DarkSurface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Store, contentDescription = "Owner", tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RESTAURANT OWNER CONSOLE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = restaurant.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Enterprise Module Shortcuts for Owner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenMenuBuilder,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Digital Menu Builder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onOpenCrm,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Guest CRM Hub", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // VIP RECOGNITION LIVE POPUP ALERT CARD
                if (showVipAlert) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = Color(0xFF451A03),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("vip_recognition_alert_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👑 PLATINUM VIP CHECK-IN DETECTED", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                }
                                IconButton(
                                    onClick = { showVipAlert = false },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sarah Jenkins • Window Booth #4", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Lifetime Visits: 18 • Spend: $2,450.00 • Fav: Truffle Tagliatelle & Wagyu", fontSize = 11.sp, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFFEF4444).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("⚠ Allergy Alert: PEANUTS & SHELLFISH • Prefers Sparkling Water", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == idx) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // OPERATIONS DASHBOARD VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text("OPERATIONS COMMAND CENTER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Key Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Today Revenue", fontSize = 10.sp, color = Color.Gray)
                                Text("$3,840.50", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                Text("+14% vs yesterday", fontSize = 9.sp, color = VegGreen)
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Kitchen Load", fontSize = 10.sp, color = Color.Gray)
                                Text("70%", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                                Text("14 Active Orders", fontSize = 9.sp, color = Color.LightGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Table Occupancy", fontSize = 10.sp, color = Color.Gray)
                                Text("73%", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF0EA5E9))
                                Text("11 of 15 Active", fontSize = 9.sp, color = Color.LightGray)
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Waitlist Queue", fontSize = 10.sp, color = Color.Gray)
                                Text("4 Groups", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFA855F7))
                                Text("Avg 15m wait", fontSize = 9.sp, color = Color.LightGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Parking Lot", fontSize = 10.sp, color = Color.Gray)
                                Text("70% Full", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                                Text("14 of 20 Slots", fontSize = 9.sp, color = Color.LightGray)
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("No-Show Rate", fontSize = 10.sp, color = Color.Gray)
                                Text("3.5%", fontSize = 16.sp, fontWeight = FontWeight.Black, color = VegGreen)
                                Text("Cancel: 4.8%", fontSize = 9.sp, color = Color.LightGray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Metrics & Satisfaction Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("PERFORMANCE SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("● Customer Satisfaction Score: 4.9 / 5.0 (98% Positive)", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("● Total Bookings Today: 22 Bookings | 16 Walk-Ins", fontSize = 12.sp, color = Color.LightGray)
                            Text("● Average Dining Duration: 48 Minutes per table", fontSize = 12.sp, color = Color.LightGray)
                        }
                    }
                }
            }
            1 -> {
                // DIGITAL TWIN FLOOR MAP VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text("LIVE 2D DIGITAL TWIN FLOOR MAP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Real-time table occupancy sensor stream", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    val tableNodes = listOf(
                        Triple("T-01", "Occupied", "4 Guests • 28m"),
                        Triple("T-02", "Available", "2 Guests • Clean"),
                        Triple("T-03", "Cleaning", "4 Guests • In Progress"),
                        Triple("T-04", "VIP Reserved", "Booth • Sarah J."),
                        Triple("T-05", "Occupied", "6 Guests • 45m"),
                        Triple("T-06", "Available", "2 Guests • Open"),
                        Triple("T-07", "Occupied", "2 Guests • 12m"),
                        Triple("T-08", "Available", "8 Guests • Patio")
                    )

                    val columns = 2
                    tableNodes.chunked(columns).forEach { rowTables ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTables.forEach { (tNo, state, desc) ->
                                val (badgeColor, borderColor) = when (state) {
                                    "Available" -> Pair(Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.5f))
                                    "Occupied" -> Pair(Color(0xFFEF4444), Color(0xFFEF4444).copy(alpha = 0.5f))
                                    "Cleaning" -> Pair(Color(0xFF0EA5E9), Color(0xFF0EA5E9).copy(alpha = 0.5f))
                                    else -> Pair(GoldPrimary, GoldPrimary.copy(alpha = 0.5f))
                                }

                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(tNo, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                            Surface(
                                                color = badgeColor.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(state, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = badgeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(desc, fontSize = 11.sp, color = Color.LightGray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Reservations
                LazyColumn(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookings) { booking ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(booking.bookingCode, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text(booking.status, fontSize = 12.sp, color = VegGreen, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${booking.guestCount} Guests • ${booking.bookingTimeSlot} on ${booking.bookingDate}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("Seating: ${booking.seatingPref} • Occasion: ${booking.occasion}", fontSize = 12.sp, color = Color.Gray)
                                if (booking.specialRequests.isNotBlank()) {
                                    Text("Note: ${booking.specialRequests}", fontSize = 11.sp, color = GoldPrimary)
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Menu Manager
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Add New Menu Item", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newDishName,
                                onValueChange = { newDishName = it },
                                label = { Text("Dish Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_dish_name")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newDishDesc,
                                onValueChange = { newDishDesc = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row {
                                OutlinedTextField(
                                    value = newDishPrice,
                                    onValueChange = { newDishPrice = it },
                                    label = { Text("Price ($)") },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = newDishCategory,
                                    onValueChange = { newDishCategory = it },
                                    label = { Text("Category") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    if (newDishName.isNotBlank()) {
                                        onAddMenuItem(newDishName, newDishDesc, newDishCategory, newDishPrice.toDoubleOrNull() ?: 18.0, newDishIsVeg)
                                        newDishName = ""
                                        newDishDesc = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add Dish to Menu", color = GoldOnPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("CURRENT MENU DINES", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)

                    Spacer(modifier = Modifier.height(10.dp))

                    menuItems.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("$${item.price} • ${item.category}", fontSize = 12.sp, color = GoldPrimary)
                                }

                                IconButton(onClick = { onDeleteMenuItem(item.id) }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // Offer Rules
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Create Custom Dining Discount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = offerTitleInput,
                                onValueChange = { offerTitleInput = it },
                                label = { Text("Offer Title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = offerPctInput,
                                onValueChange = { offerPctInput = it },
                                label = { Text("Discount Percentage (%)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    onCreateOffer(offerTitleInput, "PERCENTAGE", offerPctInput.toIntOrNull() ?: 30, 50.0)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Publish Offer to Platform", color = GoldOnPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            else -> {
                // Analytics
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("THIS MONTH PERFORMANCE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Total Table Bookings: 148", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Net Revenue Generated: $12,450.00", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VegGreen)
                            Text("Avg Rating: 4.9 ★", fontSize = 14.sp, color = GoldPrimary)
                        }
                    }
                }
            }
        }
    }
}
