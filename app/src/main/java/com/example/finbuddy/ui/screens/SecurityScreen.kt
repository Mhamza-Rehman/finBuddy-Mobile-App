package com.example.finbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security & Privacy", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SecurityInfoCard(
                title = "Data Ownership",
                text = "Your data belongs entirely to you. FinBuddy secures your records locally and uses enterprise-grade end-to-end cloud encryption via Supabase."
            )
            SecurityInfoCard(
                title = "Privacy Assurance",
                text = "We take your privacy seriously. FinBuddy does not sell, trade, or share your financial records, transaction tracking logs, or personal information with third-party advertising networks."
            )
            SecurityInfoCard(
                title = "Access Controls",
                text = "All authenticated database sessions are guarded by strict Row-Level Security policies. Only your verified biometric or login token can decrypt and read your transaction ledger."
            )
        }
    }
}

@Composable
private fun SecurityInfoCard(title: String, text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF0F9D58), fontSize = 16.sp)
            Text(text, color = Color(0xFF37474F), fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}
