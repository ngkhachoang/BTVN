package com.example.btvn_nkh.ui.components

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btvn_nkh.data.model.Photo
import com.example.btvn_nkh.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoPickerViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _photos = MutableStateFlow<List<Photo>>(emptyList())
    val photos: StateFlow<List<Photo>> = _photos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadPhotos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val photoList = photoRepository.getPhotos()
                _photos.value = photoList
            } catch (e: Exception) {
                // Handle error - could add error state if needed
                _photos.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun queryPhotoMetadata(uri: Uri) {
        viewModelScope.launch {
            try {
                // Query metadata của ảnh được chọn từ Content Provider
                // Có thể dùng để lấy thêm thông tin như size, date, etc.
                photoRepository.getPhotoMetadata(uri)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}