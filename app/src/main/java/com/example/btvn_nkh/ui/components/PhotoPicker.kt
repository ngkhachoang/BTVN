package com.example.btvn_nkh.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.btvn_nkh.data.model.Photo

@Composable
fun PhotoPicker(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PhotoPickerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Permission launcher
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // Gallery launcher - mở system gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Khi user chọn ảnh từ system gallery, ta vẫn có thể query metadata từ Content Provider
            viewModel.queryPhotoMetadata(uri)
            onImageSelected(uri)
        }
    }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Nếu có quyền, có thể load danh sách ảnh từ Content Provider (cho các mục đích khác)
            viewModel.loadPhotos()
            // Nhưng vẫn mở system gallery để user chọn
            galleryLauncher.launch("image/*")
        }
    }
    
    Column(modifier = modifier) {
        // Main image display area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .padding(vertical = 16.dp)
                .border(2.dp, Color(0xFFE040FB), RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Có quyền thì load photos từ Content Provider và mở system gallery
                        viewModel.loadPhotos()
                        galleryLauncher.launch("image/*")
                    } else {
                        // Chưa có quyền thì xin quyền
                        permissionLauncher.launch(permission)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(selectedImageUri)
                        .size(800, 800) // Resize image với Coil
                        .crossfade(true)
                        .build(),
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
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap to choose from gallery", color = Color(0xFFBDBDBD))
                }
            }
        }
    }
}