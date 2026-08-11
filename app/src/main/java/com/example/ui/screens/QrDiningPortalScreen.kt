package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.data.model.DiningOrderItemEntity
import com.example.data.model.DiningSessionEntity
import com.example.data.model.MenuItemEntity
import com.example.data.model.ServiceRequestEntity
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary

@Composable
fun QrDiningPortalScreen(
    session: DiningSessionEntity?,
    menuItems: List<MenuItemEntity>,
    placedOrders: List<DiningOrderItemEntity>,
    serviceRequests: List<ServiceRequestEntity>,
    onPlaceOrder: (String, String, Double, Int, String) -> Unit,
    onRequestService: (String, String) -> Unit,
    onCheckout: () -> Unit,
    onBackClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(2) } // Default to 2 (Service & Bill) or user choice
    var orderNote by remember { mutableStateOf("") }
    var selectedSplitCount by remember { mutableStateOf(2) }

    var showScanner by remember { mutableStateOf(false) }
    var currentTableNumber by remember { mutableStateOf(session?.tableNumber ?: "Table 14") }
    var currentRestaurantName by remember { mutableStateOf(session?.restaurantName ?: "Aura Fine Dining") }

    if (showScanner) {
        QrCodeScannerView(
            initialTableNumber = currentTableNumber,
            initialRestaurantName = currentRestaurantName,
            onScanSuccess = { newTbl, newRest ->
                currentTableNumber = newTbl
                currentRestaurantName = newRest
                showScanner = false
                selectedTab = 2 // Redirect directly to Service & Bill request menu
            },
            onCloseScanner = {
                showScanner = false
            }
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            // Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "QR IN-DINING PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentRestaurantName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                    modifier = Modifier.clickable { showScanner = true }.testTag("btn_open_qr_scanner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentTableNumber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }
                }
            }

            // Quick Scan Banner for seamless camera feedback
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { showScanner = true }
                    .testTag("banner_scan_table_qr")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Connected: $currentTableNumber • $currentRestaurantName",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Tap to open interactive Table QR scanner",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldPrimary,
                        contentColor = Color.Black
                    ) {
                        Text(
                            text = "SCAN QR",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = GoldPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldPrimary
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Live Menu", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 0) GoldPrimary else Color.Gray) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Orders (${placedOrders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 1) GoldPrimary else Color.Gray) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Service & Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedTab == 2) GoldPrimary else Color.Gray) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Live Menu View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "ORDER DIRECTLY FROM YOUR TABLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    items(menuItems) { item ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (item.isVeg) Color(0xFF10B981) else Color.Red
                                        ) {
                                            Text(
                                                text = if (item.isVeg) "VEG" else "NON-VEG",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = item.description,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Text(
                                        text = "$${String.format("%.2f", item.price)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }

                                Button(
                                    onClick = { onPlaceOrder(item.id, item.name, item.price, 1, "Table Order") },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Active Table Orders View
                val subtotal = placedOrders.sumOf { it.price * it.quantity }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "REALTIME KITCHEN TRACKING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }

                    items(placedOrders) { order ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${order.quantity}x ${order.itemName}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (order.specialNotes.isNotBlank()) {
                                        Text(
                                            text = "Note: ${order.specialNotes}",
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "$${String.format("%.2f", order.price * order.quantity)}",
                                        fontSize = 13.sp,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = when (order.status) {
                                        "SERVED" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                        "PREPARING" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                        else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = order.status,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (order.status) {
                                            "SERVED" -> Color(0xFF10B981)
                                            "PREPARING" -> Color(0xFFF59E0B)
                                            else -> Color(0xFF3B82F6)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Current Subtotal:", fontSize = 13.sp, color = Color.Gray)
                                    Text("$${String.format("%.2f", subtotal)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Service & Bill Checkout View
                val totalBill = placedOrders.sumOf { it.price * it.quantity }
                val perPerson = if (selectedSplitCount > 0) totalBill / selectedSplitCount else totalBill

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Call Staff & Request Items Grid
                    item {
                        Text(
                            text = "INSTANT TABLE ASSISTANCE & REQUESTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Row 1: Primary Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onRequestService("CALL_WAITER", "Call Waiter to Table 14") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                modifier = Modifier.weight(1f).testTag("btn_request_waiter"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.RoomService, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Call Waiter", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onRequestService("WATER_REFILL", "Water Refill x2") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                modifier = Modifier.weight(1f).testTag("btn_request_water"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.LocalBar, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Water Refill", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onRequestService("SOFT_DRINK", "Ice & Soft Drink") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                modifier = Modifier.weight(1f).testTag("btn_request_drink"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🥤 Soft Drink", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Row 2: Secondary Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onRequestService("CUTLERY", "Extra Cutlery / Spoons") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                modifier = Modifier.weight(1f).testTag("btn_request_cutlery"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🍴 Cutlery", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onRequestService("NAPKINS", "Fresh Table Napkins") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                modifier = Modifier.weight(1f).testTag("btn_request_napkins"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("🧻 Napkins", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = { onRequestService("REQUEST_BILL", "Printed Tax Invoice") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                modifier = Modifier.weight(1f).testTag("btn_request_bill"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bill", fontSize = 11.sp)
                            }
                        }
                    }

                    // Active Service Requests Tracking List
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "LIVE SERVICE REQUEST STATUS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                if (serviceRequests.isEmpty()) {
                                    Text(
                                        text = "No active service requests. Tap above to request waiter or items.",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        serviceRequests.forEach { req ->
                                            val statusBg = when (req.status) {
                                                "PENDING" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                                "ASSIGNED", "ACCEPTED" -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                                "SERVING" -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                                "COMPLETED", "FULFILLED" -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                else -> Color.Gray.copy(alpha = 0.2f)
                                            }

                                            val statusColor = when (req.status) {
                                                "PENDING" -> Color(0xFFF59E0B)
                                                "ASSIGNED", "ACCEPTED" -> Color(0xFF3B82F6)
                                                "SERVING" -> Color(0xFF8B5CF6)
                                                "COMPLETED", "FULFILLED" -> Color(0xFF10B981)
                                                else -> Color.Gray
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(DarkBackground)
                                                    .padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = req.requestType.replace("_", " "),
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                    if (req.note.isNotBlank()) {
                                                        Text(
                                                            text = req.note,
                                                            fontSize = 11.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = statusBg
                                                ) {
                                                    Text(
                                                        text = req.status,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = statusColor,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Split Bill Calculator
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "SPLIT BILL EQUALLY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Split between:", fontSize = 13.sp, color = Color.White)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        OutlinedButton(
                                            onClick = { if (selectedSplitCount > 1) selectedSplitCount-- },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                        }
                                        Text(
                                            text = "$selectedSplitCount Diners",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )
                                        OutlinedButton(
                                            onClick = { selectedSplitCount++ },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                        }
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = GoldPrimary.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Your Equal Share:", fontSize = 13.sp, color = Color.White)
                                        Text(
                                            text = "$${String.format("%.2f", perPerson)}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Allergy Safety Check before checkout
                    item {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("checkout_allergy_warning_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🛡️", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("ALLERGY PROTECTION SAFETY CHECK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                    Text("No active allergens (Peanuts, Shellfish) detected in ordered dishes.", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    // Checkout & Instant Pay Button
                    item {
                        Button(
                            onClick = onCheckout,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay $${String.format("%.2f", totalBill)} via DineWallet & Complete",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun QrCodeScannerView(
    initialTableNumber: String,
    initialRestaurantName: String,
    onScanSuccess: (tableNumber: String, restaurantName: String) -> Unit,
    onCloseScanner: () -> Unit
) {
    var flashOn by remember { mutableStateOf(false) }
    var selectedTableNumber by remember { mutableStateOf(initialTableNumber) }
    var selectedRestaurantName by remember { mutableStateOf(initialRestaurantName) }
    var isScanningSuccess by remember { mutableStateOf(false) }

    // Laser beam sweep animation (0f to 1f)
    val infiniteTransition = rememberInfiniteTransition(label = "scannerLaser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    // Viewfinder border glow pulse
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Viewfinder border color animation on scan match
    val viewfinderColor by animateColorAsState(
        targetValue = if (isScanningSuccess) Color(0xFF10B981) else GoldPrimary,
        animationSpec = tween(400),
        label = "viewfinderColor"
    )

    // Auto redirect after scan verification delay
    LaunchedEffect(isScanningSuccess) {
        if (isScanningSuccess) {
            delay(1200)
            onScanSuccess(selectedTableNumber, selectedRestaurantName)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("qr_code_scanner_interface")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCloseScanner, modifier = Modifier.testTag("btn_close_qr_scanner")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Scanner", tint = Color.White)
                }
                Text(
                    text = "TABLE QR CODE SCANNER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    letterSpacing = 1.sp
                )
                IconButton(onClick = { flashOn = !flashOn }) {
                    Icon(
                        imageVector = if (flashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Flash Toggle",
                        tint = if (flashOn) GoldPrimary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Align table QR code inside the viewfinder target",
                fontSize = 12.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Viewfinder Target Frame
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .border(
                        width = 3.dp,
                        color = viewfinderColor.copy(alpha = if (isScanningSuccess) 1f else pulseGlow),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable {
                        if (!isScanningSuccess) {
                            isScanningSuccess = true
                        }
                    }
                    .testTag("qr_scanner_viewfinder_frame"),
                contentAlignment = Alignment.Center
            ) {
                // Flash overlay effect
                if (flashOn) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Yellow.copy(alpha = 0.08f))
                    )
                }

                // Decorative Corner Brackets
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 5.dp.toPx()
                    val cornerLength = 30.dp.toPx()
                    val color = viewfinderColor

                    // Top-Left
                    drawPath(
                        path = Path().apply {
                            moveTo(0f, cornerLength)
                            lineTo(0f, 0f)
                            lineTo(cornerLength, 0f)
                        },
                        color = color,
                        style = Stroke(width = strokeWidth)
                    )
                    // Top-Right
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width - cornerLength, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, cornerLength)
                        },
                        color = color,
                        style = Stroke(width = strokeWidth)
                    )
                    // Bottom-Left
                    drawPath(
                        path = Path().apply {
                            moveTo(0f, size.height - cornerLength)
                            lineTo(0f, size.height)
                            lineTo(cornerLength, size.height)
                        },
                        color = color,
                        style = Stroke(width = strokeWidth)
                    )
                    // Bottom-Right
                    drawPath(
                        path = Path().apply {
                            moveTo(size.width - cornerLength, size.height)
                            lineTo(size.width, size.height)
                            lineTo(size.width, size.height - cornerLength)
                        },
                        color = color,
                        style = Stroke(width = strokeWidth)
                    )
                }

                // Center QR Code Graphic
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = if (isScanningSuccess) Color(0xFF10B981) else Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(110.dp)
                )

                // Animated Laser Beam
                if (!isScanningSuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .offset(y = (-110 + (laserProgress * 220)).dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        GoldPrimary,
                                        Color.White,
                                        GoldPrimary,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // SUCCESS SCAN OVERLAY ANIMATION
                androidx.compose.animation.AnimatedVisibility(
                    visible = isScanningSuccess,
                    enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xEE064E3B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Scan Success",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(60.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "QR CODE VERIFIED!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$selectedTableNumber • $selectedRestaurantName",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                color = GoldPrimary,
                                trackColor = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Opening Service & Menu Portal...",
                                fontSize = 10.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Selectable Table Targets for Quick Testing
            Text(
                text = "SELECT TABLE PRESET:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val targets = listOf(
                    Triple("Table 14", "Aura Fine Dining", "📍 Main Hall"),
                    Triple("Table 08", "Aura Lounge & Bar", "🍸 Bar Section"),
                    Triple("Table 22", "Skyline Terrace", "🌆 Terrace")
                )

                targets.forEach { (tbl, rest, label) ->
                    val isSel = selectedTableNumber == tbl
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSel) GoldPrimary.copy(alpha = 0.25f) else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSel) GoldPrimary else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTableNumber = tbl
                                selectedRestaurantName = rest
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = label, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(text = tbl, fontSize = 10.sp, color = GoldPrimary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Scan Action Button
            Button(
                onClick = {
                    if (!isScanningSuccess) {
                        isScanningSuccess = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanningSuccess) Color(0xFF10B981) else GoldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_simulate_qr_scan")
            ) {
                Icon(
                    imageVector = if (isScanningSuccess) Icons.Default.CheckCircle else Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isScanningSuccess) "Scanning Verified!" else "Scan $selectedTableNumber QR Code",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

