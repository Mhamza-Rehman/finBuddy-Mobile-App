package com.example.finbuddy.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finbuddy.data.model.Transaction
import com.example.finbuddy.ui.viewmodel.ActivityUiState
import com.example.finbuddy.ui.viewmodel.ActivityViewModel
import com.example.finbuddy.ui.viewmodel.SelectedFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(navController: NavController, viewModel: ActivityViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) { viewModel.fetchTransactions() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { BottomNavigationBar(navController, currentScreen = "activity") }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(innerPadding).padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            val query = (uiState as? ActivityUiState.Success)?.searchQuery ?: ""
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search transactions...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(20.dp))
            val selected = (uiState as? ActivityUiState.Success)?.selectedFilter ?: SelectedFilter.ALL
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActivityFilterChip(selected == SelectedFilter.ALL, "All") { viewModel.onFilterChange(SelectedFilter.ALL) }
                ActivityFilterChip(selected == SelectedFilter.DEPOSITS, "Deposits") { viewModel.onFilterChange(SelectedFilter.DEPOSITS) }
                ActivityFilterChip(selected == SelectedFilter.WITHDRAWALS, "Withdrawals") { viewModel.onFilterChange(SelectedFilter.WITHDRAWALS) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            when (val state = uiState) {
                ActivityUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) }
                is ActivityUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message, color = MaterialTheme.colorScheme.error) }
                is ActivityUiState.Success -> {
                    val listState = rememberLazyListState()
                    LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.groups.forEach { group ->
                            item(key = "header_${group.header}") { ActivitySectionHeader(group.header) }
                            items(group.transactions, key = { it.id ?: "${it.category}-${it.timestamp}" }) { tx ->
                                TransactionRow(tx, viewModel.formatTime(tx))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    val brandGreen = Color(0xFF0F9D58)
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) brandGreen else MaterialTheme.colorScheme.surface,
        border = if (!selected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)) else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            color = if (selected) Color.White else brandGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActivitySectionHeader(text: String) {
    Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 8.dp)) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, timeText: String) {
    val isExpense = transaction.type.equals("Expense", ignoreCase = true)
    val amountColor = if (isExpense) Color(0xFF0F9D58) else Color(0xFFE53935)
    val amountPrefix = if (isExpense) "-" else "+"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.category, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
                Text("${transaction.source} • $timeText", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Text("$amountPrefix$${"%.2f".format(transaction.amount)}", color = amountColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
