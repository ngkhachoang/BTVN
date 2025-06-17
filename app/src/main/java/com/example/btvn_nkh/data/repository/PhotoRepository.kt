package com.example.btvn_nkh.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.example.btvn_nkh.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {

    companion object {
        private val PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
        )
    }

    suspend fun getPhotos(limit: Int = 50): List<Photo> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            "$sortOrder LIMIT $limit"
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val photo = createPhotoFromCursor(cursor)
                photos.add(photo)
            }
        }

        photos
    }

    suspend fun getPhotoMetadata(uri: Uri): Photo? = withContext(Dispatchers.IO) {
        contentResolver.query(
            uri,
            PROJECTION,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return@withContext createPhotoFromCursor(cursor, uri)
            }
        }
        null
    }

    private fun createPhotoFromCursor(
        cursor: android.database.Cursor,
        customUri: Uri? = null
    ): Photo {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

        val id = cursor.getLong(idColumn)
        val displayName = cursor.getString(displayNameColumn)
        val dateAdded = cursor.getLong(dateAddedColumn)
        val size = cursor.getLong(sizeColumn)
        val mimeType = cursor.getString(mimeTypeColumn)

        val uri = customUri ?: ContentUris.withAppendedId(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            id
        )

        return Photo(
            id = id,
            uri = uri,
            displayName = displayName,
            dateAdded = dateAdded,
            size = size,
            mimeType = mimeType
        )
    }
}