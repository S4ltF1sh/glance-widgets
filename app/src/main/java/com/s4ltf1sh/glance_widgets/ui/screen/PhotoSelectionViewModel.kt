package com.s4ltf1sh.glance_widgets.ui.screen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s4ltf1sh.glance_widgets.db.WidgetModelRepository
import com.s4ltf1sh.glance_widgets.db.photo.Media
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.model.photo.WidgetPhotoData
import com.s4ltf1sh.glance_widgets.utils.EventChannel
import com.s4ltf1sh.glance_widgets.utils.HasEventFlow
import com.s4ltf1sh.glance_widgets.utils.getImages
import com.s4ltf1sh.glance_widgets.widget.widget.photo.PhotosWidgetWorker
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoSelectionUiState(
    val allImages: List<Media> = emptyList(),
    val selectedPhotos: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val hasPermission: Boolean = false,
    val error: String? = null
)

sealed interface PhotoSelectionEvent {
    data object CloseActivity : PhotoSelectionEvent
}

@HiltViewModel
class PhotoSelectionViewModel @Inject constructor(
    private val widgetRepository: WidgetModelRepository,
    private val moshi: Moshi,
    private val eventChannel: EventChannel<PhotoSelectionEvent>
) : ViewModel(), HasEventFlow<PhotoSelectionEvent> by eventChannel {

    private val _uiState = MutableStateFlow(
        PhotoSelectionUiState(selectedPhotos = emptySet())
    )
    val uiState: StateFlow<PhotoSelectionUiState> = _uiState.asStateFlow()

    fun onPermissionGranted(context: Context) {
        _uiState.value = _uiState.value.copy(hasPermission = true)
        loadImages(context)
    }

    fun onPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            hasPermission = false,
            error = "Permission denied. Cannot access photos."
        )
        Log.d("PhotoSelectionViewModel", "Permission denied")
    }

    fun loadImages(context: Context) {
        if (!_uiState.value.hasPermission) return

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    error = null
                )

                val images = context.getImages()

                _uiState.value = _uiState.value.copy(
                    allImages = images,
                    isLoading = false
                )

                Log.d("PhotoSelectionViewModel", "Loaded ${images.size} images")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load images: ${e.message}"
                )
                Log.e("PhotoSelectionViewModel", "Error loading images", e)
            }
        }
    }

    fun togglePhotoSelection(photoPath: String) {
        val currentSelected = _uiState.value.selectedPhotos
        val newSelected = if (currentSelected.contains(photoPath)) {
            currentSelected - photoPath
        } else {
            currentSelected + photoPath
        }

        _uiState.value = _uiState.value.copy(selectedPhotos = newSelected)
        Log.d("PhotoSelectionViewModel", "Selected photos: ${newSelected.size}")
    }

    fun addPhotosFromPicker(uris: List<String>) {
        if (uris.isNotEmpty()) {
            val newSelected = _uiState.value.selectedPhotos + uris
            _uiState.value = _uiState.value.copy(selectedPhotos = newSelected)
            Log.d("PhotoSelectionViewModel", "Added ${uris.size} photos from picker")
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedPhotos = emptySet())
    }

    fun selectAll() {
        val allUris = _uiState.value.allImages.map { it.uri.toString() }.toSet()
        _uiState.value = _uiState.value.copy(selectedPhotos = allUris)
    }

    fun getSelectedPhotos(): List<String> {
        return _uiState.value.selectedPhotos.toList()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshImages(context: Context) {
        loadImages(context)
    }

    fun setDataForWidget(context: Context, widgetId: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val widget = widgetRepository.getWidget(widgetId)
            ?: run {
                Log.e("PhotoSelectionViewModel", "Widget not found with ID: $widgetId")
                return@launch
            }

        val widgetPhotoData = WidgetPhotoData(photoPaths = _uiState.value.selectedPhotos.toList())

        val updatedWidget = widget.copy(
            type = WidgetType.PHOTO,
            data = moshi.adapter(WidgetPhotoData::class.java).toJson(widgetPhotoData),
        )

        widgetRepository.updateWidget(updatedWidget)

        // Enqueue the worker to update the widget with selected photos
        // Enqueue once to ensure it runs immediately
        PhotosWidgetWorker.enqueue(context, widgetId, widget.size)

        _uiState.update { it.copy(isLoading = false) }
        eventChannel.send(PhotoSelectionEvent.CloseActivity)
    }
}