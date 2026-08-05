package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentInd
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.model.StaffTaskEntity
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary

data class StaffMember(
    val id: String,
    val name: String,
    val role: String,
    val status: String, // ONLINE, BUSY, AWAY, OFFLINE
    val activeTaskCount: Int
)

@Composable
fun ManagerDashboardScreen(
    tasks: List<StaffTaskEntity>,
    onAssignTask: (taskId: String, staffId: String, staffName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val staffList = remember {
        listOf(
            StaffMember("staff_101", "Alex Waiter", "Senior Waiter", "ONLINE", 1),
            StaffMember("staff_102", "Sarah Waiter", "Floor Staff", "BUSY", 2),
            StaffMember("staff_103", "John Runner", "Food Runner", "ONLINE", 0),
            StaffMember("staff_104", "Emma Sommelier", "Sommelier", "AWAY", 0)
        )
    }

    val pendingTasks = tasks.filter { it.taskStatus == "PENDING" }
    val activeTasks = tasks.filter { it.taskStatus == "ACCEPTED" || it.taskStatus == "IN_PROGRESS" }
    val completedCount = tasks.count { it.taskStatus == "COMPLETED" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Manager Header Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldPrimary.copy(alpha = 0.2f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Queue,
                                    contentDescription = null,
                                    tint = GoldPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "MANAGER DASHBOARD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Text(
                                text = "Live Service Request & Task Queue",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // KPI Overview Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard("Pending Queue", "${pendingTasks.size}", Color(0xFFF59E0B), Modifier.weight(1f))
                    KpiCard("Active Tasks", "${activeTasks.size}", Color(0xFF3B82F6), Modifier.weight(1f))
                    KpiCard("Avg Speed", "2.4 min", Color(0xFF10B981), Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Staff Status Bar
        Text(
            text = "FLOOR STAFF AVAILABILITY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            staffList.forEach { staff ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = staff.name.split(" ").first(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = when (staff.status) {
                                "ONLINE" -> "🟢 Online"
                                "BUSY" -> "🟡 Busy (${staff.activeTaskCount})"
                                else -> "⚪ Away"
                            },
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Queue Section
        Text(
            text = "LIVE TASK QUEUE & REASSIGNMENT",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                ManagerTaskQueueCard(
                    task = task,
                    staffList = staffList,
                    onReassign = { staff ->
                        onAssignTask(task.id, staff.id, staff.name)
                    }
                )
            }
        }
    }
}

@Composable
fun ManagerTaskQueueCard(
    task: StaffTaskEntity,
    staffList: List<StaffMember>,
    onReassign: (StaffMember) -> Unit
) {
    var showStaffMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
            .testTag("manager_task_card_${task.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.tableNumber,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (task.priority == "URGENT") Color(0xFFEF4444) else Color(0xFF3B82F6).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = task.priority,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (task.priority == "URGENT") Color.White else Color(0xFF3B82F6),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (task.taskStatus) {
                        "PENDING" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                        "COMPLETED" -> Color(0xFF10B981).copy(alpha = 0.2f)
                        else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = task.taskStatus,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (task.taskStatus) {
                            "PENDING" -> Color(0xFFF59E0B)
                            "COMPLETED" -> Color(0xFF10B981)
                            else -> Color(0xFF3B82F6)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Request: ${task.requestedItemsSummary}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assigned to: ${task.assignedStaffName.ifEmpty { "Unassigned" }}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Box {
                    OutlinedButton(
                        onClick = { showStaffMenu = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_reassign_${task.id}")
                    ) {
                        Icon(imageVector = Icons.Default.AssignmentInd, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reassign", fontSize = 11.sp, color = GoldPrimary)
                    }

                    DropdownMenu(
                        expanded = showStaffMenu,
                        onDismissRequest = { showStaffMenu = false }
                    ) {
                        staffList.forEach { staff ->
                            DropdownMenuItem(
                                text = { Text("${staff.name} (${staff.status})") },
                                onClick = {
                                    onReassign(staff)
                                    showStaffMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
