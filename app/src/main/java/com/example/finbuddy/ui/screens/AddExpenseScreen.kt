package com.example.finbuddy.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.EditCalendar
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.finbuddy.ui.viewmodel.TransactionUiState
import com.example.finbuddy.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavController,
    viewModel: TransactionViewModel = viewModel()
) {
    var amount by remember { mutableStateOf("0.00") }
    var selectedType by remember { mutableStateOf("Expense") }
    var selectedCategory by remember { mutableStateOf("FOOD") }
    var note by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState = viewModel.uiState

    val categories = listOf(
        CategoryItem("FOOD", Icons.Default.Restaurant, Color(0xFF0F9D58)),
        CategoryItem("TRANSPORT", Icons.Default.DirectionsCar, Color(0xFF2196F3)),
        CategoryItem("SHOPPING", Icons.Default.ShoppingBag, Color(0xFF673AB7)),
        CategoryItem("RENT", Icons.Default.Home, Color(0xFFFF9800)),
        CategoryItem("OTHER", Icons.Default.Category, Color(0xFF607D8B))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Transaction",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1A1C1E)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(
                            Icons.Rounded.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Expense/Income Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFF1F3F4))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selectedType == "Expense") Color(0xFF0F9D58) else Color.Transparent)
                        .clickable { selectedType = "Expense" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Expense",
                        color = if (selectedType == "Expense") Color.White else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (selectedType == "Income") Color(0xFF0F9D58) else Color.Transparent)
                        .clickable { selectedType = "Income" },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Income",
                        color = if (selectedType == "Income") Color.White else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Amount Section
            Text(
                text = "AMOUNT",
                fontSize = 12.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F9D58)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() || char == '.' }) {
                            amount = it 
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E),
                        textAlign = TextAlign.Center
                    ),
                    singleLine = true,
                    modifier = Modifier.width(IntrinsicSize.Min)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Category Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Category",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Scrollable Categories
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(categories) { category ->
                    CategoryItemView(
                        item = category,
                        isSelected = selectedCategory == category.name,
                        onClick = { selectedCategory = category.name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Fields Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TransactionDetailField(
                    label = "DATE",
                    value = "October 24, 2023",
                    icon = Icons.Default.CalendarToday,
                    iconColor = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF2196F3),
                    trailingIcon = Icons.Rounded.EditCalendar
                )

                TransactionDetailField(
                    label = "NOTES (OPTIONAL)",
                    value = if (note.isEmpty()) "Add a description..." else note,
                    icon = Icons.Default.Notes,
                    iconColor = Color(0xFFF1F3F4),
                    iconTint = Color.Gray,
                    isNote = true,
                    onNoteChange = { note = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Transaction Button
            Button(
                onClick = {
                    viewModel.saveTransaction(
                        amountStr = amount,
                        type = selectedType,
                        category = selectedCategory
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                enabled = uiState != TransactionUiState.Loading
            ) {
                if (uiState == TransactionUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        "Save Transaction",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class CategoryItem(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun CategoryItemView(
    item: CategoryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF0F9D58) else Color(0xFFF1F3F4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = if (isSelected) Color.White else Color(0xFF5F6368),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}

@Composable
fun TransactionDetailField(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    iconTint: Color,
    trailingIcon: ImageVector? = null,
    isNote: Boolean = false,
    onNoteChange: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold
                )
                if (isNote) {
                    BasicTextField(
                        value = if (value == "Add a description...") "" else value,
                        onValueChange = onNoteChange,
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        ),
                        decorationBox = { innerTextField ->
                            if (value == "Add a description...") {
                                Text(
                                    text = "Add a description...",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray
                                )
                            }
                            innerTextField()
                        }
                    )
                } else {
                    Text(
                        text = value,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C1E)
                    )
                }
            }
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
