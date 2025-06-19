package com.example.btvn_nkh.ai_art.network

import com.example.btvn_nkh.ai_art.exception.AiArtException
import com.example.btvn_nkh.ai_art.exception.ErrorReason
import com.example.btvn_nkh.network.SignatureApiManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUploadManager @Inject constructor(
    private val aiArtApiService: AiArtApiService
) {

    suspend fun uploadImage(file: File): Result<String> {
        return try {
            val preSignedLink = getPreSignedLink()
            if (preSignedLink.isFailure) {
                return preSignedLink.map { "" }
            }

            val linkData = preSignedLink.getOrThrow()
            val uploadResult = uploadToServer(file, linkData.url)

            if (uploadResult.isSuccess) {
                Result.success(linkData.path)
            } else {
                Result.failure(AiArtException(ErrorReason.GenerateImageError))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getPreSignedLink(): Result<Link> {
        return try {
            val response = aiArtApiService.getPreSignedLink()

            if (response.isSuccessful && response.body()?.data != null) {
                response.body()?.timestamp?.let {
                    SignatureApiManager.setTimeStamp(it)
                }
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(AiArtException(ErrorReason.PresignedLinkError))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadToServer(file: File, url: String): Result<Unit> {
        return try {
            val requestBody = file.asRequestBody(MEDIA_TYPE_JPEG)
            val response = aiArtApiService.pushImageToServer(url, requestBody)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private val MEDIA_TYPE_JPEG = "image/jpeg".toMediaTypeOrNull()
    }
}