package com.example.btvn_nkh.ai_art.network

import com.example.btvn_nkh.ai_art.exception.AiArtException
import com.example.btvn_nkh.ai_art.exception.ErrorReason
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtGenerationService @Inject constructor(
    private val aiArtApiService: AiArtApiService
) {

    suspend fun generateArt(
        imagePath: String,
        styleId: String? = null,
        positivePrompt: String? = null,
        negativePrompt: String? = null
    ): Result<String> {
        return try {
            val request = createRequest(imagePath, styleId, positivePrompt, negativePrompt)
            val response = aiArtApiService.generateAiArt(request)

            if (response.isSuccessful) {
                val resultUrl = response.body()?.data?.url
                if (resultUrl.isNullOrEmpty()) {
                    Result.failure(AiArtException(ErrorReason.GenerateImageError))
                } else {
                    Result.success(resultUrl)
                }
            } else {
                Result.failure(AiArtException(ErrorReason.GenerateImageError))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createRequest(
        imagePath: String,
        styleId: String?,
        positivePrompt: String?,
        negativePrompt: String?
    ): AiArtRequest {
        return AiArtRequest(
            file = imagePath,
            styleId = styleId,
            positivePrompt = positivePrompt,
            negativePrompt = negativePrompt
        )
    }
}