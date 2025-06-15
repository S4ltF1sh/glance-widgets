package com.s4ltf1sh.glance_widgets.ui.screen

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.s4ltf1sh.glance_widgets.db.photo.Media
import com.s4ltf1sh.glance_widgets.utils.SingleEventEffect
import com.s4ltf1sh.glance_widgets.utils.UriPermissionHelper
import com.s4ltf1sh.glance_widgets.utils.getFilePathFromUriCawc

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoSelectionScreen(
    widgetId: Int,
    onBackPressed: () -> Unit,
    onCloseActivity: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: PhotoSelectionViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsState()
    SingleEventEffect(
        viewModel.eventFlow
    ) { event ->
        handleFollowScreenEvent(event, onCloseActivity)
    }

    val readMediaImagesState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            listOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(READ_MEDIA_IMAGES)
        } else {
            listOf(READ_EXTERNAL_STORAGE)
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.all { it.value }) {
            viewModel.onPermissionGranted(context)
        } else {
            viewModel.onPermissionDenied()
        }
    }

//    val pickMedia = rememberLauncherForActivityResult(
//        ActivityResultContracts.PickMultipleVisualMedia()
//    ) { uris ->
//        if (uris.isNotEmpty()) {
//            val newPhotos = uris.mapNotNull { context.getFilePathFromUriCawc(it) }
//            viewModel.addPhotosFromPicker(newPhotos)
//        }
//    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            // Take persistent permission for each URI
            val successfulUris = uris.filter { uri ->
                UriPermissionHelper.takePersistentPermission(context, uri)
            }

            Log.d("PhotoSelectionScreen", "Granted permissions for ${successfulUris.size} URIs")

            if (successfulUris.isNotEmpty()) {
                val newPhotos = successfulUris.mapNotNull { context.getFilePathFromUriCawc(it) }
                Log.d("PhotoSelectionScreen", "Adding ${newPhotos.size} new photos from picker")
                viewModel.addPhotosFromPicker(newPhotos)
            }
        }
    }


    // Handle permission state changes
    LaunchedEffect(readMediaImagesState.allPermissionsGranted) {
        if (readMediaImagesState.allPermissionsGranted) {
            viewModel.onPermissionGranted(context)
        } else if (!readMediaImagesState.shouldShowRationale) {
            // Request permissions
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(READ_MEDIA_IMAGES)
            } else {
                arrayOf(READ_EXTERNAL_STORAGE)
            }
            requestPermissionLauncher.launch(permissions)
        }
    }

    // Show error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // You can show a SnackBar here if needed
            viewModel.clearError()
        }
    }

    Content(
        uiState = uiState,
        onPhotoToggle = { photoPath ->
            viewModel.togglePhotoSelection(photoPath)
        },
        onSetDataForWidgets = {
            viewModel.setDataForWidget(context, widgetId = widgetId)
        },
        onSelectImage = {
            documentPickerLauncher.launch(arrayOf("image/*"))
        },
        onBackPressed = onBackPressed,
        onRequestPermission = {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(READ_MEDIA_IMAGES)
            } else {
                arrayOf(READ_EXTERNAL_STORAGE)
            }
            requestPermissionLauncher.launch(permissions)
        },
        onRefresh = { viewModel.refreshImages(context) },
        onSelectAll = viewModel::selectAll,
        onClearSelection = viewModel::clearSelection
    )
}

private fun handleFollowScreenEvent(
    event: PhotoSelectionEvent,
    onCloseActivity: () -> Unit
) {
    when (event) {
        PhotoSelectionEvent.CloseActivity -> onCloseActivity()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: PhotoSelectionUiState,
    onPhotoToggle: (String) -> Unit,
    onSetDataForWidgets: () -> Unit,
    onSelectImage: () -> Unit,
    onBackPressed: () -> Unit,
    onRequestPermission: () -> Unit,
    onRefresh: () -> Unit,
    onSelectAll: () -> Unit,
    onClearSelection: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select Photos",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.hasPermission && uiState.allImages.isNotEmpty()) {
                        IconButton(onClick = onRefresh) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }

                        if (uiState.selectedPhotos.size < uiState.allImages.size) {
                            IconButton(onClick = onSelectAll) {
                                Icon(Icons.Default.Check, contentDescription = "Select All")
                            }
                        }
                    }

                    if (uiState.selectedPhotos.isNotEmpty()) {
                        TextButton(onClick = onClearSelection) {
                            Text("Clear")
                        }
                    }
                }
            )
        },
        bottomBar = {
            ActionButtons(
                selectImage = onSelectImage,
                setDataForWidget = onSetDataForWidgets,
                selectedCount = uiState.selectedPhotos.size,
                hasPermission = uiState.hasPermission
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Selection counter
            if (uiState.selectedPhotos.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "${uiState.selectedPhotos.size} photos selected",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            when {
                !uiState.hasPermission -> {
                    PermissionRequestContent(onRequestPermission = onRequestPermission)
                }

                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = onRefresh
                    )
                }

                uiState.allImages.isEmpty() -> {
                    EmptyContent()
                }

                else -> {
                    PhotoGrid(
                        images = uiState.allImages,
                        selectedPhotos = uiState.selectedPhotos,
                        onPhotoToggle = onPhotoToggle
                    )
                }
            }
        }
    }
}

@Composable
private fun PhotoGrid(
    images: List<Media>,
    selectedPhotos: Set<String>,
    onPhotoToggle: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(images) { media ->
            PhotoItem(
                media = media,
                isSelected = selectedPhotos.contains(media.path),
                onClick = { onPhotoToggle(media.path) }
            )
        }
    }
}

@Composable
private fun PhotoItem(
    media: Media,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        3.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(media.path)
                .crossfade(true)
                .build(),
            contentDescription = media.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp)
                    )
            )

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    selectImage: () -> Unit,
    setDataForWidget: () -> Unit,
    selectedCount: Int,
    hasPermission: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = selectImage,
                modifier = Modifier.weight(1f),
                enabled = hasPermission
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add More")
            }

            Button(
                onClick = setDataForWidget,
                enabled = selectedCount > 0,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Set Widget ($selectedCount)")
            }
        }
    }
}

@Composable
private fun PermissionRequestContent(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📷",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We need access to your photos to display them in the widget.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading photos...")
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "❌",
            style = MaterialTheme.typography.displayMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📱",
                style = MaterialTheme.typography.displayMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No photos found",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try using the photo picker to add some photos",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}