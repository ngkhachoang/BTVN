package com.example.btvn_nkh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ResultScreen(imageUrl: String, navController: NavController) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<MainScreenViewModel>()
    val isSaving by viewModel.isSaving.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(imageUrl) {
        viewModel.setGeneratedImageUrl(imageUrl)
        viewModel.clearError()
        viewModel.clearSaveSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(
            onClick = {
                viewModel.clearGeneratedImageUrl()
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back to Main Screen",
                tint = Color(0xFF7C79FF)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Generated AI Art",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.saveGeneratedImage() },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isSaving) Color(0xFF7C79FF) else Color(0xFFCCCCCC)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Downloading...", color = Color.White)
                } else {
                    Text("Download Photo", color = Color.White, fontSize = 18.sp)
                }
            }
            if (saveSuccess) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Image saved successfully!", color = Color(0xFF2E7D32), fontSize = 16.sp)
            }
            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(error, color = Color(0xFFD32F2F), fontSize = 16.sp)
            }
        }
    }
}