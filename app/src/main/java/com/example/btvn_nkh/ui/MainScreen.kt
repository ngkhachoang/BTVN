// File: MainScreen.kt
package com.example.btvn_nkh.ui

import android.net.Uri

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.btvn_nkh.ui.components.PhotoPicker

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel,
    navController: NavController
) {
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()
    val authenticationStatus by viewModel.authenticationStatus.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generatedImageUrl by viewModel.generatedImageUrl.collectAsState()
    val (selectedTabIndex, setSelectedTabIndex) = remember(tabs) { mutableIntStateOf(0) }
    val (selectedStyleId, setSelectedStyleId) = remember { mutableStateOf<String?>(null) }
    var prompt by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadStyles()
    }

    LaunchedEffect(generatedImageUrl) {
        generatedImageUrl?.let { url ->
            navController.navigate("result?url=${Uri.encode(url)}")
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearError() }) {
                        Text("×", fontSize = 20.sp, color = Color(0xFFD32F2F))
                    }
                }
            }
        }

        authenticationStatus?.let { status ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = status,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.clearAuthStatus() }) {
                        Text("×", fontSize = 20.sp, color = Color(0xFF2E7D32))
                    }
                }
            }
        }
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            placeholder = { Text("Enter your prompt…") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(2.dp, Color(0xFFE040FB), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        )
        PhotoPicker(
            selectedImageUri = selectedImageUri,
            onImageSelected = { uri -> viewModel.setSelectedImageUri(uri) }
        )
        Text(
            "Choose your Style",
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFFE040FB)
        )
        if (tabs.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(tabs) { idx, tab ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { setSelectedTabIndex(idx) }
                    ) {
                        Text(
                            tab.name,
                            color = if (idx == selectedTabIndex) Color(0xFFE040FB) else Color(0xFF222222),
                            fontWeight = if (idx == selectedTabIndex) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                        if (idx == selectedTabIndex) {
                            Box(
                                Modifier
                                    .height(3.dp)
                                    .width(32.dp)
                                    .background(Color(0xFFE040FB), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(Modifier.height(3.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs[selectedTabIndex].styles) { style ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .clickable { setSelectedStyleId(style._id) }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(style.key),
                            contentDescription = style.name ?: "",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(
                                    width = if (selectedStyleId == style._id) 3.dp else 1.dp,
                                    color = if (selectedStyleId == style._id) Color(0xFFE040FB) else Color(
                                        0xFFE0E0E0
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .shadow(8.dp, RoundedCornerShape(12.dp))
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            style.name ?: "",
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF444444),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.testAuthentication() },
                enabled = !isAuthenticating,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isAuthenticating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Test Auth", color = Color.White)
                }
            }

            Button(
                onClick = {
                    viewModel.generateAiArt(selectedStyleId, prompt)
                },
                enabled = selectedImageUri != null && !isGenerating,
                modifier = Modifier
                    .weight(2f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedImageUri != null && !isGenerating) Color(0xFFE040FB) else Color(0xFFCCCCCC)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating...", color = Color.White)
                } else {
                    Text("Generate AI Art", color = Color.White)
                }
            }
        }
    }
}
