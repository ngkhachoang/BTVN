package com.example.btvn_nkh.ai_art.models

import android.net.Uri

data class AiArtParams(
    val imageUri: Uri,
    val styleId: String? = null,
    val positivePrompt: String? = null,
    val negativePrompt: String? = null
)

object ImageConfig {
    const val MAX_DIMENSION = 1024
    const val MIN_DIMENSION = 128
    const val JPEG_QUALITY = 90
    val SUPPORTED_MIME_TYPES = listOf("image/jpeg", "image/jpg")
}