package com.example.finbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
fun HelpCenterScreen(navController: NavController) {
    val faqs = listOf(
        "How does FinBuddy track my expenses automatically?" to
            "FinBuddy securely scans your incoming SMS notification logs locally on your device to parse transactional updates from your banks or digital wallets instantly.",
        "Is my banking information secure?" to
            "Yes. FinBuddy reads transaction notices from your text alerts but never requests or stores your direct bank login credentials, account passwords, or PIN codes.",
        "What is the Financial Health Score?" to
            "It is a dynamic rating from 0 to 1000 calculated based on your local savings ratios and tracking consistency to help measure budget stability.",
        "Can I access my data offline?" to
            "Yes, your latest synchronized transactions are cached on-device so you can review your budget metrics even without an active internet connection.",
        "How do I backup or export my data?" to
            "Your data is automatically backed up securely to your encrypted cloud profile. Dedicated CSV data exporting features are currently in development."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            faqs.forEach { (q, a) ->
                FaqItem(question = q, answer = a)
            }
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1C1E),
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFF0F9D58)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(answer, color = Color(0xFF455A64), fontSize = 13.sp, lineHeight = 19.sp)
            }
        }
    }
}
