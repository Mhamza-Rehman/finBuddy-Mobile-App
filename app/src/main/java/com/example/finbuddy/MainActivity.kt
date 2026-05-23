package com.example.finbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finbuddy.data.local.ThemeSettings
import com.example.finbuddy.ui.theme.FinBuddyTheme
import com.example.finbuddy.ui.screens.SplashScreen
import com.example.finbuddy.ui.screens.LoginScreen
import com.example.finbuddy.ui.screens.DashboardScreen
import com.example.finbuddy.ui.screens.AddExpenseScreen
import com.example.finbuddy.ui.screens.ProfileScreen
import com.example.finbuddy.ui.screens.GetStartedScreen
import com.example.finbuddy.ui.screens.SettingsScreen
import com.example.finbuddy.ui.screens.SignupScreen
import com.example.finbuddy.ui.screens.ActivityScreen
import com.example.finbuddy.ui.screens.ProfileInfoScreen
import com.example.finbuddy.ui.screens.SecurityScreen
import com.example.finbuddy.ui.screens.HelpCenterScreen
import com.example.finbuddy.ui.screens.AboutScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeSettings = remember { ThemeSettings(applicationContext) }
            val isDarkMode by themeSettings.isDarkModeEnabled.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            FinBuddyTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    isDarkMode = isDarkMode,
                    onDarkModeChange = { enabled ->
                        scope.launch { themeSettings.setDarkModeEnabled(enabled) }
                    }
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("get_started") {
            GetStartedScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignupScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("activity") {
            ActivityScreen(navController)
        }
        composable("add_expense") {
            AddExpenseScreen(navController)
        }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange
            )
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("profile_info") {
            ProfileInfoScreen(navController)
        }
        composable("security") {
            SecurityScreen(navController)
        }
        composable("help_center") {
            HelpCenterScreen(navController)
        }
        composable("about_finbuddy") {
            AboutScreen(navController)
        }
    }
}
