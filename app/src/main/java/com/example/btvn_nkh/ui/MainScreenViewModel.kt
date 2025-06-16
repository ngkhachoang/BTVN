package com.example.btvn_nkh.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btvn_nkh.data.model.StyleTabDto
import com.example.btvn_nkh.data.repository.StyleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: StyleRepository
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _tabs = MutableStateFlow<List<StyleTabDto>>(emptyList())
    val tabs: StateFlow<List<StyleTabDto>> = _tabs

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun loadStyles() {
        viewModelScope.launch {
            try {
                val result = repository.getStyles()
                _tabs.value = result
            } catch (e: Exception) {
                // catch error
            }
        }
    }
}
