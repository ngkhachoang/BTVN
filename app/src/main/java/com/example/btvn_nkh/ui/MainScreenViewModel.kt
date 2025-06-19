package com.example.btvn_nkh.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btvn_nkh.ai_art.exception.AiArtException
import com.example.btvn_nkh.ai_art.models.AiArtParams
import com.example.btvn_nkh.ai_art.usecase.AiArtUseCase
import com.example.btvn_nkh.data.model.StyleTabDto
import com.example.btvn_nkh.data.repository.StyleRepository
import com.example.btvn_nkh.network.SignatureRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: StyleRepository,
    private val signatureRepository: SignatureRepository,
    private val aiArtUseCase: AiArtUseCase
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri

    private val _tabs = MutableStateFlow<List<StyleTabDto>>(emptyList())
    val tabs: StateFlow<List<StyleTabDto>> = _tabs

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating

    private val _authenticationStatus = MutableStateFlow<String?>(null)
    val authenticationStatus: StateFlow<String?> = _authenticationStatus

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _generatedImageUrl = MutableStateFlow<String?>(null)
    val generatedImageUrl: StateFlow<String?> = _generatedImageUrl

    fun loadStyles() {
        viewModelScope.launch {
            try {
                val result = repository.getStyles()
                _tabs.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load styles: ${e.message}"
            }
        }
    }

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
        _errorMessage.value = null
        _generatedImageUrl.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearAuthStatus() {
        _authenticationStatus.value = null
    }

    fun generateAiArt(
        styleId: String? = null,
        positivePrompt: String? = null,
        negativePrompt: String? = null
    ) {
        val imageUri = _selectedImageUri.value
        if (imageUri == null) {
            _errorMessage.value = "Please select an image first"
            return
        }

        viewModelScope.launch {
            try {
                _isGenerating.value = true
                _errorMessage.value = null
                _generatedImageUrl.value = null

                val params = AiArtParams(
                    imageUri = imageUri,
                    styleId = styleId,
                    positivePrompt = positivePrompt,
                    negativePrompt = negativePrompt
                )

                val result = aiArtUseCase.generateAiArt(params)
                result.fold(
                    onSuccess = { imageUrl: String ->
                        _generatedImageUrl.value = imageUrl
                    },
                    onFailure = { exception: Throwable ->
                        val errorMsg = when (exception) {
                            is AiArtException -> exception.errorReason.name
                            else -> exception.message ?: "Unknown error occurred"
                        }
                        _errorMessage.value = errorMsg
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Failed to generate AI art: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun testAuthentication() {
        viewModelScope.launch {
            try {
                _isAuthenticating.value = true
                _authenticationStatus.value = null
                _errorMessage.value = null

                val result = signatureRepository.testAuthentication()
                result.fold(
                    onSuccess = { message ->
                        _authenticationStatus.value = message
                    },
                    onFailure = { exception ->
                        _errorMessage.value = "Authentication failed: ${exception.message}"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Authentication error: ${e.message}"
            } finally {
                _isAuthenticating.value = false
            }
        }
    }
}
