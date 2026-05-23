package com.example.finbuddy.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController, currentScreen: String) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == "dashboard",
            onClick = { 
                if (currentScreen != "dashboard") {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            },
            icon = { Icon(Icons.Default.GridView, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            selected = currentScreen == "activity",
            onClick = { 
                if (currentScreen != "activity") {
                    navController.navigate("activity")
                }
            },
            icon = { Icon(Icons.Default.History, contentDescription = "Activity") },
            label = { Text("Activity") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            selected = currentScreen == "settings",
            onClick = { 
                if (currentScreen != "settings") {
                    navController.navigate("settings")
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                indicatorColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
