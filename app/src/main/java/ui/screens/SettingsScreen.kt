package com.example.finbuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
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
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, currentScreen = "settings")
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

            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Image placeholder
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFDE7D0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = Color(0xFF8B4513)
                        )
                        // Online indicator
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F9D58))
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Error",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1A1C1E)
                        )
                        Text(
                            text = "Error4455@example.com",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ACCOUNT Section
            SettingsSectionHeader("ACCOUNT")
            SettingsItem(
                icon = Icons.Default.Person,
                title = "Profile Information",
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF0F9D58),
                onClick = { navController.navigate("profile") }
            )
            SettingsItem(
                icon = Icons.Default.Security,
                title = "Security",
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF0F9D58),
                onClick = { /* Navigate */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PREFERENCES Section
            SettingsSectionHeader("PREFERENCES")
//            SettingsItem(
//                icon = Icons.Default.Notifications,
//                title = "Notifications",
//                iconContainerColor = Color(0xFFE8F5E9),
//                iconColor = Color(0xFF0F9D58),
//                onClick = { /* Navigate */ }
//            )
            SettingsItem(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF0F9D58),
                trailing = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { isDarkMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF0F9D58)
                        )
                    )
                }
            )
            SettingsItem(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = "English (US)",
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF0F9D58),
                onClick = { /* Navigate */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SUPPORT Section
            SettingsSectionHeader("SUPPORT")
            SettingsItem(
                icon = Icons.Default.HelpCenter,
                title = "Help Center",
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF0F9D58),
                onClick = { /* Navigate */ }
            )
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About FinBuddy",
                iconContainerColor = Color(0xFFE8F5E9),
                iconColor = Color(0xFF0F9D58),
                onClick = { /* Navigate */ }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SettingsSectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F9D58).copy(alpha = 0.7f),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconContainerColor: Color,
    iconColor: Color,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1C1E)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF0F9D58)
                )
            }
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
