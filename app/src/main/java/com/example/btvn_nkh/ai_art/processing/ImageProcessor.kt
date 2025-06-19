package com.example.btvn_nkh.ai_art.processing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.graphics.scale
import com.example.btvn_nkh.ai_art.exception.AiArtException
import com.example.btvn_nkh.ai_art.exception.ErrorReason
import com.example.btvn_nkh.ai_art.models.ImageConfig
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageProcessor @Inject constructor(
    private val context: Context
) {

    fun processImage(uri: Uri): Result<File> {
        return try {
            validateImageFormat(uri)
                .mapCatching { uriToBitmap(uri) }
                .mapCatching { resizeBitmap(it) }
                .mapCatching { saveBitmapToCache(it) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateImageFormat(uri: Uri): Result<Uri> {
        val mimeType = context.contentResolver.getType(uri)
        return if (mimeType in ImageConfig.SUPPORTED_MIME_TYPES) {
            Result.success(uri)
        } else {
            Result.failure(AiArtException(ErrorReason.ImageTypeInvalid))
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        context.contentResolver.openInputStream(uri).use { inputStream ->
            return BitmapFactory.decodeStream(inputStream)
                ?: throw IllegalArgumentException("Unable to decode image")
        }
    }

    private fun resizeBitmap(originalBitmap: Bitmap): Bitmap {
        val width = originalBitmap.width
        val height = originalBitmap.height
        val scale = calculateScale(width, height)

        if (scale == 1.0f) return originalBitmap

        val newWidth = (width * scale).toInt().coerceIn(
            ImageConfig.MIN_DIMENSION,
            ImageConfig.MAX_DIMENSION
        )
        val newHeight = (height * scale).toInt().coerceIn(
            ImageConfig.MIN_DIMENSION,
            ImageConfig.MAX_DIMENSION
        )

        return originalBitmap.scale(newWidth, newHeight)
    }

    private fun calculateScale(width: Int, height: Int): Float {
        return when {
            width in ImageConfig.MIN_DIMENSION..ImageConfig.MAX_DIMENSION &&
                    height in ImageConfig.MIN_DIMENSION..ImageConfig.MAX_DIMENSION -> 1.0f

            width < ImageConfig.MIN_DIMENSION || height < ImageConfig.MIN_DIMENSION -> {
                maxOf(
                    ImageConfig.MIN_DIMENSION.toFloat() / width,
                    ImageConfig.MIN_DIMENSION.toFloat() / height
                )
            }

            else -> {
                minOf(
                    ImageConfig.MAX_DIMENSION.toFloat() / width,
                    ImageConfig.MAX_DIMENSION.toFloat() / height
                )
            }
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): File {
        val fileName = "ai_art_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, ImageConfig.JPEG_QUALITY, outputStream)
        }

        return file
    }
}