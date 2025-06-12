package com.s4ltf1sh.glance_widgets.utils

import android.app.DownloadManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.use

fun Context.getFilePathFromUri(uri: Uri?): String? {
    uri ?: return null

    // Xử lý Document URIs (Android 4.4+)
    if (DocumentsContract.isDocumentUri(this, uri)) {

        when (uri.authority) {
            // External Storage Documents
            "com.android.externalstorage.documents" -> {
                val documentId = DocumentsContract.getDocumentId(uri)
                val parts = documentId.split(":")
                val type = parts[0]

                if (type.equals("primary", ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/${parts[1]}"
                }
            }

            // Downloads Documents
            "com.android.providers.downloads.documents" -> {
                val documentId = DocumentsContract.getDocumentId(uri)

                if (!TextUtils.isEmpty(documentId)) {
                    return try {
                        val id = documentId.toLong()
                        val downloadsUri = ContentUris.withAppendedId(
                            "content://downloads/public_downloads".toUri(), id
                        )
                        getDataColumn(downloadsUri, null, null)
                    } catch (e: NumberFormatException) {
                        null
                    }
                }
            }

            // Media Documents (Images, Videos, Audio)
            "com.android.providers.media.documents" -> {
                val documentId = DocumentsContract.getDocumentId(uri)
                val parts = documentId.split(":")
                val type = parts[0]
                val id = parts[1]

                val contentUri = when (type) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> null
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(id)

                return getDataColumn(contentUri, selection, selectionArgs)
            }
        }
    }

    // Content URIs (MediaStore, etc.)
    else if (uri.scheme.equals("content", ignoreCase = true)) {
        return when (uri.authority) {
            // Google Photos content provider
            "com.google.android.apps.photos.content" -> uri.lastPathSegment
            // Các content provider khác
            else -> getDataColumn(uri, null, null)
        }
    }

    // File URIs
    else if (uri.scheme.equals("file", ignoreCase = true)) {
        return uri.path
    }

    return null
}

private fun Context.getDataColumn(
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    uri ?: return null

    val column = "_data"
    val projection = arrayOf(column)

    return try {
        contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                cursor.getString(columnIndex)
            } else null
        }
    } catch (e: Exception) {
        // Log error if needed
        null
    }
}

fun Context.downloadImageUsingMediaStore(imageUrl: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val uri = imageUrl.toUri()
        val resolver = contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, uri.lastPathSegment)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AMXS")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val imageUri =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (imageUri != null) {
            val outputStream = resolver.openOutputStream(imageUri)
            if (outputStream != null) {
                try {
                    val inputStream = URL(imageUrl).openStream()
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@downloadImageUsingMediaStore,
                            "Image downloaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@downloadImageUsingMediaStore,
                            "Failed to download image",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@downloadImageUsingMediaStore,
                    "Failed to create MediaStore entry",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}

fun Context.downloadImageUsingDownloadManager(imageUrl: String) {
    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = imageUrl.toUri()
    val request = DownloadManager.Request(uri)

    // Allow the media scanner to scan the downloaded file
    request.allowScanningByMediaScanner()
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
    request.setDestinationInExternalPublicDir(
        Environment.DIRECTORY_DOWNLOADS,
        uri.lastPathSegment
    )

    downloadManager.enqueue(request)
    Toast.makeText(
        this@downloadImageUsingDownloadManager,
        "Downloading Image...",
        Toast.LENGTH_SHORT
    ).show()
}

suspend fun Context.downloadImage(
    url: String,
    applicationContext: Context,
    force: Boolean
): String {
    val request = ImageRequest.Builder(this)
        .data(url)
        .build()

    // Request the image to be loaded and throw error if it failed
    with(imageLoader) {
        if (force) {
            diskCache?.remove(url)
            memoryCache?.remove(MemoryCache.Key(url))
        }
        val result = execute(request)
        if (result is ErrorResult) {
            throw result.throwable
        }
    }

    // Get the path of the loaded image from DiskCache.
    val path = imageLoader.diskCache?.openSnapshot(url)?.use { snapshot ->
        val imageFile = snapshot.data.toFile()

        // Use the FileProvider to create a content URI
        val contentUri = getUriForFile(
            this,
            "${applicationContext.packageName}.provider",
            imageFile,
        )

        // Find the current launcher every time to ensure it has read permissions
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY,
            )
        }
        val launcherName = resolveInfo?.activityInfo?.packageName
        if (launcherName != null) {
            grantUriPermission(
                launcherName,
                contentUri,
                FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_PERSISTABLE_URI_PERMISSION,
            )
        }

        // return the path
        contentUri.toString()
    }

    return requireNotNull(path) {
        "Couldn't find cached file"
    }
}