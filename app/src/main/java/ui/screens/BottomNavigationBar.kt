package com.example.finbuddy.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController, currentScreen: String) {
    NavigationBar(
        containerColor = Color.White,
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
                selectedIconColor = Color(0xFF0F9D58),
                selectedTextColor = Color(0xFF0F9D58),
                indicatorColor = Color(0xFFE8F5E9)
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
                selectedIconColor = Color(0xFF0F9D58),
                selectedTextColor = Color(0xFF0F9D58),
                indicatorColor = Color(0xFFE8F5E9)
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
                selectedIconColor = Color(0xFF0F9D58),
                selectedTextColor = Color(0xFF0F9D58),
                indicatorColor = Color(0xFFE8F5E9)
            )
        )
    }
}
