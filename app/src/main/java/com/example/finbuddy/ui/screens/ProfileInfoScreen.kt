package com.example.finbuddy.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.finbuddy.ui.viewmodel.ProfileInfoUiState
import com.example.finbuddy.ui.viewmodel.ProfileInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoScreen(
    navController: NavController,
    viewModel: ProfileInfoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadProfile() }
    LaunchedEffect(uiState) {
        if (uiState is ProfileInfoUiState.Saved) {
            navController.popBackStack()
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) viewModel.uploadAvatar(context, uri)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Information", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            ProfileInfoUiState.Loading, ProfileInfoUiState.Saving -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0xFF0F9D58)) }
            }
            is ProfileInfoUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = Color(0xFFB3261E)) }
            }
            is ProfileInfoUiState.Editing -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8F9FA))
                        .padding(innerPadding)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.BottomEnd) {
                            if (!state.profile.avatarUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = state.profile.avatarUrl,
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(120.dp).clip(CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF0F9D58), Color(0xFF66BB6A))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
                                }
                            }
                            IconButton(
                                onClick = {
                                    picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit avatar", tint = Color(0xFF0F9D58))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.fullNameInput,
                        onValueChange = viewModel::onFullNameChanged,
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.phoneInput,
                        onValueChange = viewModel::onPhoneChanged,
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Text(
                        text = state.profile.email,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = { viewModel.saveProfile() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
                    ) { Text("Save", color = Color.White, fontWeight = FontWeight.Bold) }
                }
            }
            ProfileInfoUiState.Saved -> Unit
        }
    }
}
