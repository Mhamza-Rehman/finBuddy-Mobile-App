package com.example.finbuddy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finbuddy.R
import com.example.finbuddy.ui.viewmodel.AnalyticsUiState
import com.example.finbuddy.ui.viewmodel.AnalyticsViewModel
import com.example.finbuddy.ui.viewmodel.DashboardUiState
import com.example.finbuddy.ui.viewmodel.DashboardViewModel
import androidx.compose.runtime.collectAsState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,
    analyticsViewModel: AnalyticsViewModel = viewModel(),
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val analyticsState by analyticsViewModel.uiState.collectAsState()
    val dashboardState by dashboardViewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            dashboardViewModel.refreshMetrics()
            analyticsViewModel.fetchFinancialScore()
        }
    }

    val metrics = (dashboardState as? DashboardUiState.Success)?.metrics
    val totalBalanceText = formatUsd(metrics?.totalBalance ?: 0.0)
    val incomeText = formatUsd(metrics?.totalIncome ?: 0.0)
    val expenseText = formatUsd(metrics?.totalExpenses ?: 0.0)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController, currentScreen = "dashboard")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logo_pic),
                            contentDescription = null,
                            tint = Color(0xFF0F9D58),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "FinBuddy",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
                
                // Profile Image Placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFDE7D0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color(0xFF8B4513)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Total Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Balance",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = totalBalanceText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F9D58)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "2.4%",
                                    color = Color(0xFF0F9D58),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF0F9D58),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "from last month",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Income and Expenses Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Income Card
                SummaryCard(
                    title = "Income",
                    amount = incomeText,
                    icon = Icons.Default.ArrowDownward,
                    iconColor = Color(0xFF0F9D58),
                    backgroundColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
                
                // Expenses Card
                SummaryCard(
                    title = "Expenses",
                    amount = expenseText,
                    icon = Icons.Default.ArrowUpward,
                    iconColor = Color(0xFFE53935),
                    backgroundColor = Color(0xFFFFEBEE),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Add Transaction Button
            OutlinedButton(
                onClick = { navController.navigate("add_expense") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF0F9D58)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0F9D58))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Add Transaction",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Analytics Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Analytics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F3F4)
                ) {
                    Text(
                        text = "6 Months",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AnalyticsScoreSection(
                state = analyticsState,
                onRetry = { analyticsViewModel.fetchFinancialScore() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun formatUsd(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(amount)
}

@Composable
private fun AnalyticsScoreSection(
    state: AnalyticsUiState,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        when (state) {
            AnalyticsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0F9D58))
                }
            }
            is AnalyticsUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Could not load financial score",
                        color = Color(0xFFB3261E),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.message, color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) { Text("Retry") }
                }
            }
            is AnalyticsUiState.Success -> {
                val result = state.result
                val tagColor = when (result.tier) {
                    "Excellent", "Good" -> Color(0xFF0F9D58)
                    "Fair" -> Color(0xFFFFB300)
                    else -> Color(0xFFE53935)
                }
                val tagBackground = when (result.tier) {
                    "Excellent", "Good" -> Color(0xFFE8F5E9)
                    "Fair" -> Color(0xFFFFF8E1)
                    else -> Color(0xFFFFEBEE)
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Financial Health Score",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${result.score}/1000",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1C1E)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = tagBackground
                    ) {
                        Text(
                            text = result.tier,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            color = tagColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = amount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1C1E)
                )
            }
        }
    }
}
