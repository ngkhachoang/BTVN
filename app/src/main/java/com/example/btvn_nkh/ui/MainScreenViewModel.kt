package com.example.btvn_nkh.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainScreenViewModel : ViewModel() {
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }
} 