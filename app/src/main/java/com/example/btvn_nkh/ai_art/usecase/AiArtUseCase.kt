package com.example.btvn_nkh.ai_art.usecase

import com.example.btvn_nkh.ai_art.exception.AiArtException
import com.example.btvn_nkh.ai_art.exception.ErrorReason
import com.example.btvn_nkh.ai_art.models.AiArtParams
import com.example.btvn_nkh.ai_art.network.ArtGenerationService
import com.example.btvn_nkh.ai_art.network.ImageUploadManager
import com.example.btvn_nkh.ai_art.processing.ImageProcessor
import com.example.btvn_nkh.ai_art.storage.FileManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiArtUseCase @Inject constructor(
    private val imageProcessor: ImageProcessor,
    private val imageUploadManager: ImageUploadManager,
    private val artGenerationService: ArtGenerationService,
    private val fileManager: FileManager
) {

    suspend fun generateAiArt(params: AiArtParams): Result<String> {
        return try {
            val processedFile = imageProcessor.processImage(params.imageUri)
            if (processedFile.isFailure) {
                return processedFile.map { "" }
            }

            val imageFile = processedFile.getOrThrow()
            val uploadResult = imageUploadManager.uploadImage(imageFile)
            if (uploadResult.isFailure) {
                return Result.failure(AiArtException(ErrorReason.GenerateImageError))
            }

            val imagePath = uploadResult.getOrThrow()
            artGenerationService.generateArt(
                imagePath = imagePath,
                styleId = params.styleId,
                positivePrompt = params.positivePrompt,
                negativePrompt = params.negativePrompt
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveImage(imageUrl: String): Result<Unit> {
        return fileManager.saveImageToDownloads(imageUrl)
    }
}