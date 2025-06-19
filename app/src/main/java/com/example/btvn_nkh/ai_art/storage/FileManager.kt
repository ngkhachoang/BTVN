package com.example.btvn_nkh.ai_art.storage

import android.os.Environment
import com.example.btvn_nkh.ai_art.exception.AiArtException
import com.example.btvn_nkh.ai_art.exception.ErrorReason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor() {

    suspend fun saveImageToDownloads(imageUrl: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val connection = createConnection(imageUrl)

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext Result.failure(AiArtException(ErrorReason.UnknownError))
                }

                val inputStream = connection.inputStream
                val outputFile = createOutputFile()

                saveStreamToFile(inputStream, outputFile)
                inputStream.close()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun createConnection(imageUrl: String): HttpURLConnection {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        return connection
    }

    private fun createOutputFile(): File {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        return File(downloadsDir, "ai_art_${System.currentTimeMillis()}.jpg")
    }

    private fun saveStreamToFile(inputStream: InputStream, outputFile: File) {
        FileOutputStream(outputFile).use { outputStream ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
        }
    }

    companion object {
        private const val BUFFER_SIZE = 4096
    }
}