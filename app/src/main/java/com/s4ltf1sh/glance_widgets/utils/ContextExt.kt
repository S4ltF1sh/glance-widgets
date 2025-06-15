package com.s4ltf1sh.glance_widgets.utils

import android.app.DownloadManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.net.toUri
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.s4ltf1sh.glance_widgets.db.photo.Media
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.security.MessageDigest
import kotlin.io.copyTo
import kotlin.use

fun Context.getFilePathFromUriCawc(uri: Uri?): String? {
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

suspend fun Context.getRandomImage(url: String, force: Boolean = false): Bitmap? {
    val request = ImageRequest.Builder(this).data(url).apply {
        if (force) {
            memoryCachePolicy(CachePolicy.DISABLED)
            diskCachePolicy(CachePolicy.DISABLED)
        }
    }.build()

    // Request the image to be loaded and throw error if it failed
    return when (val result = imageLoader.execute(request)) {
        is ErrorResult -> throw result.throwable
        is SuccessResult -> result.drawable.toBitmapOrNull()
    }
}

@OptIn(ExperimentalCoilApi::class)
suspend fun Context.downloadImage(
    url: String,
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


private const val TAG = "CoilImageDownload"
private const val HIDDEN_IMAGE_FOLDER = ".widget_images"

/**
 * Download image using Coil library - more efficient with built-in caching
 */
suspend fun Context.downloadImageWithCoil(
    url: String,
    force: Boolean = false
): String = withContext(Dispatchers.IO) {
    try {
        // Create hidden folder
        val hiddenFolder = File(filesDir, HIDDEN_IMAGE_FOLDER)
        if (!hiddenFolder.exists()) {
            hiddenFolder.mkdirs()
        }

        val filename = generateFilename(url)
        val imageFile = File(hiddenFolder, filename)

        // Check if already exists
        if (!force && imageFile.exists() && imageFile.length() > 0) {
            Log.d(TAG, "Using cached image: ${imageFile.absolutePath}")
            return@withContext imageFile.absolutePath
        }

        // Download using Coil
        val imageLoader = ImageLoader.Builder(this@downloadImageWithCoil)
            .crossfade(false)
            .build()

        val request = ImageRequest.Builder(this@downloadImageWithCoil)
            .data(url)
            .allowHardware(false) // Important for saving bitmap
            .build()

        val result = imageLoader.execute(request)

        if (result is SuccessResult) {
            // Save bitmap to file
            val bitmap = result.drawable.toBitmap()
            saveBitmapToFile(bitmap, imageFile)

            Log.d(TAG, "Image saved with Coil: ${imageFile.absolutePath}")
            return@withContext imageFile.absolutePath
        } else {
            Log.e(TAG, "Coil download failed")
            ""
        }

    } catch (e: Exception) {
        Log.e(TAG, "Error downloading with Coil", e)
        ""
    }
}

/**
 * Extension function to convert Drawable to Bitmap
 */
private fun android.graphics.drawable.Drawable.toBitmap(): Bitmap {
    if (this is android.graphics.drawable.BitmapDrawable) {
        return bitmap
    }

    val bitmap = createBitmap(intrinsicWidth, intrinsicHeight)

    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)

    return bitmap
}

/**
 * Save bitmap to file with compression
 */
private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
    val tempFile = File(file.parent, "${file.name}.tmp")

    FileOutputStream(tempFile).use { out ->
        // Determine format from extension
        val format = Bitmap.CompressFormat.PNG

        // Compress with high quality
        bitmap.compress(format, 95, out)
    }

    // Rename temp to final
    if (!tempFile.renameTo(file)) {
        tempFile.delete()
        throw Exception("Failed to save image file")
    }
}

/**
 * Optimized version with size constraints for widgets
 */
suspend fun Context.downloadImageForWidget(
    url: String,
    maxWidth: Int = 1080,
    maxHeight: Int = 1080,
    force: Boolean = false
): String = withContext(Dispatchers.IO) {
    try {
        val hiddenFolder = File(filesDir, HIDDEN_IMAGE_FOLDER)
        if (!hiddenFolder.exists()) {
            hiddenFolder.mkdirs()
        }

        // Include size in filename for different widget sizes
        val filename = generateFilenameWithSize(url, maxWidth, maxHeight)
        val imageFile = File(hiddenFolder, filename)

        if (!force && imageFile.exists() && imageFile.length() > 0) {
            return@withContext imageFile.absolutePath
        }

        val imageLoader = ImageLoader.Builder(this@downloadImageForWidget)
            .crossfade(false)
            .build()

        val request = ImageRequest.Builder(this@downloadImageForWidget)
            .data(url)
            .size(maxWidth, maxHeight) // Resize for widget
            .allowHardware(false)
            .build()

        val result = imageLoader.execute(request)

        if (result is SuccessResult) {
            val bitmap = result.drawable.toBitmap()
            saveBitmapToFile(bitmap, imageFile)
            return@withContext imageFile.absolutePath
        }

        ""
    } catch (e: Exception) {
        Log.e(TAG, "Error downloading widget image", e)
        ""
    }
}

private fun generateFilenameWithSize(url: String, width: Int, height: Int): String {
    val baseHash = generateFilename(url).substringBeforeLast('.')
    val extension = url.substringAfterLast('.', "jpg").substringBefore('?')
    return "${baseHash}_${width}x${height}.$extension"
}

/**
 * Generate unique filename from URL using MD5 hash
 */
private fun generateFilename(url: String): String {
    return try {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(url.toByteArray())
        val hash = digest.joinToString("") { "%02x".format(it) }

        // Extract file extension from URL if possible
        val extension = url.substringAfterLast('.', "")
            .substringBefore('?')
            .takeIf { it.length in 2..4 } ?: "jpg"

        "$hash.$extension"
    } catch (e: Exception) {
        // Fallback to timestamp-based name
        "img_${System.currentTimeMillis()}.jpg"
    }
}

// Run the querying logic in a coroutine outside of the main thread to keep the app responsive.
// Keep in mind that this code snippet is querying only images of the shared storage.
suspend fun Context.getImages(): List<Media> = withContext(Dispatchers.IO) {
    val projection = arrayOf(
        Images.Media._ID,
        Images.ImageColumns.DATA,
        Images.Media.DISPLAY_NAME,
        Images.Media.SIZE,
        Images.Media.MIME_TYPE,
    )

    val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Query all the device storage volumes instead of the primary only
        Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        Images.Media.EXTERNAL_CONTENT_URI
    }

    val images = mutableListOf<Media>()

    contentResolver.query(
        collectionUri,
        projection,
        null,
        null,
        "${Images.Media.DATE_ADDED} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(Images.Media._ID)
        val dataColumn = cursor.getColumnIndexOrThrow(Images.ImageColumns.DATA)
        val displayNameColumn = cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME)
        val sizeColumn = cursor.getColumnIndexOrThrow(Images.Media.SIZE)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(Images.Media.MIME_TYPE)

        while (cursor.moveToNext()) {
            val uri = ContentUris.withAppendedId(collectionUri, cursor.getLong(idColumn))
            val path = cursor.getString(dataColumn)
            val name = cursor.getString(displayNameColumn)
            val size = cursor.getLong(sizeColumn)
            val mimeType = cursor.getString(mimeTypeColumn)

            val image = Media(uri, path ?: "undefined", name, size, mimeType)
            images.add(image)
        }
    }

    return@withContext images
}