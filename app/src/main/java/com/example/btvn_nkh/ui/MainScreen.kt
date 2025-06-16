// File: MainScreen.kt
package com.example.btvn_nkh.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel
) {
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val tabs by viewModel.tabs.collectAsState()
    val (selectedTabIndex, setSelectedTabIndex) = remember(tabs) { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.setSelectedImageUri(uri) }

    LaunchedEffect(Unit) {
        viewModel.loadStyles()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prompt
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Enter your prompt…") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .border(2.dp, Color(0xFFE040FB), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp)
        )

        // Chọn ảnh
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .padding(vertical = 16.dp)
                .border(2.dp, Color(0xFFE040FB), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color(0xFFBDBDBD)
                    )
                    Text("Add your photo", color = Color(0xFFBDBDBD))
                }
            }
        }

        // Style Tabs
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
            // Style Items
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tabs[selectedTabIndex].styles) { style ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(80.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(style.key),
                            contentDescription = style.name ?: "",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                        listOf(Color(0xFFEC63FF), Color(0xFF7C79FF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Generate AI", color = Color.White)
        }
    }
}
