package com.example.btvn_nkh.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
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
        private const val TAG = "PhotoRepository"
        private val PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
        )
    }

    suspend fun getPhotos(limit: Int = 100): List<Photo> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<Photo>()
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val uris = listOf(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )

        for (uri in uris) {
            try {
                contentResolver.query(
                    uri,
                    PROJECTION,
                    null,
                    null,
                    sortOrder
                )?.use { cursor ->
                    var count = 0
                    while (cursor.moveToNext() && count < limit) {
                        try {
                            val photo = createPhotoFromCursor(cursor, uri)
                            photos.add(photo)
                            count++
                        } catch (e: Exception) {
                            continue
                        }
                    }
                } ?: run {
                    Log.w(TAG, "Query for $uri returned null cursor")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error querying photos from $uri", e)
            }
        }
        photos
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
        val displayName = cursor.getString(displayNameColumn) ?: "Unknown"
        val dateAdded = cursor.getLong(dateAddedColumn)
        val size = cursor.getLong(sizeColumn)
        val mimeType = cursor.getString(mimeTypeColumn) ?: "image/*"

        val uri = ContentUris.withAppendedId(
            customUri ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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