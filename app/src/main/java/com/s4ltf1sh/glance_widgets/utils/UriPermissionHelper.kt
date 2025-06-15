package com.s4ltf1sh.glance_widgets.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object UriPermissionHelper {
    
    /**
     * Take persistent URI permission for the given URI
     */
    fun takePersistentPermission(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("UriPermissionHelper", "Persistent permission granted for: $uri")
            true
        } catch (e: SecurityException) {
            Log.e("UriPermissionHelper", "Failed to take persistent permission for: $uri", e)
            false
        }
    }
    
    /**
     * Release persistent URI permission for the given URI
     */
    fun releasePersistentPermission(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.releasePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("UriPermissionHelper", "Persistent permission released for: $uri")
            true
        } catch (e: SecurityException) {
            Log.e("UriPermissionHelper", "Failed to release persistent permission for: $uri", e)
            false
        }
    }
    
    /**
     * Check if we have persistent permission for the given URI
     */
    fun hasPersistentPermission(context: Context, uri: Uri): Boolean {
        val persistedUris = context.contentResolver.persistedUriPermissions
        return persistedUris.any { it.uri == uri && it.isReadPermission }
    }
    
    /**
     * Get all persisted URIs that we have permission for
     */
    fun getPersistedUris(context: Context): List<Uri> {
        return context.contentResolver.persistedUriPermissions
            .filter { it.isReadPermission }
            .map { it.uri }
    }
    
    /**
     * Clean up invalid persistent permissions
     */
    fun cleanupInvalidPermissions(context: Context): Int {
        val persistedUris = getPersistedUris(context)
        var cleanedCount = 0
        
        persistedUris.forEach { uri ->
            try {
                // Try to access the URI to see if it's still valid
                context.contentResolver.openInputStream(uri)?.close()
            } catch (e: Exception) {
                // URI is no longer valid, release permission
                releasePersistentPermission(context, uri)
                cleanedCount++
                Log.d("UriPermissionHelper", "Cleaned up invalid URI: $uri")
            }
        }
        
        return cleanedCount
    }
    
    /**
     * Validate that a URI string is accessible
     */
    fun isUriAccessible(context: Context, uriString: String): Boolean {
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Filter out inaccessible URIs from a list
     */
    fun filterAccessibleUris(context: Context, uriStrings: List<String>): List<String> {
        return uriStrings.filter { isUriAccessible(context, it) }
    }
}