package com.example.finbuddy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Activity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, currentScreen = "activity")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = { },
                placeholder = { Text("Search transactions...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF0F9D58))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActivityFilterChip(selected = true, label = "All")
                ActivityFilterChip(selected = false, label = "Deposits")
                ActivityFilterChip(selected = false, label = "Withdrawals")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Headers (Placeholders for list)
            ActivitySectionHeader("TODAY")
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) // Placeholder for Today's transactions

            ActivitySectionHeader("YESTERDAY")
            Box(modifier = Modifier.fillMaxWidth().height(150.dp)) // Placeholder for Yesterday's transactions

            ActivitySectionHeader("OCTOBER 24")
            Box(modifier = Modifier.fillMaxWidth().height(100.dp)) // Placeholder for older transactions
        }
    }
}

@Composable
fun ActivityFilterChip(selected: Boolean, label: String) {
    Surface(
        modifier = Modifier.clickable { },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF0F9D58) else Color.White,
        border = if (!selected) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (selected) Color.White else Color(0xFF0F9D58),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActivitySectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF9E9E9E),
        modifier = Modifier.padding(vertical = 12.dp)
    )
}
