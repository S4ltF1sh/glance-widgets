package com.s4ltf1sh.glance_widgets.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.s4ltf1sh.glance_widgets.db.calendar.WidgetCalendarData
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.utils.CalendarUtils
import com.s4ltf1sh.glance_widgets.utils.SingleEventEffect
import com.s4ltf1sh.glance_widgets.utils.UriPermissionHelper
import com.s4ltf1sh.glance_widgets.utils.getFilePathFromUriCawc

@Composable
fun CalendarSelectionScreen(
    widgetId: Int,
    widgetSize: WidgetSize,
    calendarType: WidgetType.Calendar,
    onBackPressed: () -> Unit,
    onCalendarConfigured: (WidgetCalendarData) -> Unit
) {
    val context = LocalContext.current
    val viewModel: CalendarSelectionViewModel = hiltViewModel()
    
    val uiState by viewModel.uiState.collectAsState()
    
    SingleEventEffect(viewModel.eventFlow) { event ->
        handleCalendarSelectionEvent(event, onCalendarConfigured)
    }
    
    val documentPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            if (UriPermissionHelper.takePersistentPermission(context, it)) {
                context.getFilePathFromUriCawc(it)?.let { path ->
                    viewModel.setBackgroundImage(path)
                }
            }
        }
    }
    
    Content(
        uiState = uiState,
        widgetSize = widgetSize,
        calendarType = calendarType,
        onBackPressed = onBackPressed,
        onMonthChanged = viewModel::setMonth,
        onYearChanged = viewModel::setYear,
        onDaySelected = viewModel::selectDay,
        onBackgroundImageSelect = {
            documentPickerLauncher.launch(arrayOf("image/*"))
        },
        onRemoveBackgroundImage = {
            viewModel.setBackgroundImage(null)
        },
        onConfirm = {
            viewModel.confirmCalendarConfiguration(context, widgetId, calendarType, widgetSize)
        }
    )
}

private fun handleCalendarSelectionEvent(
    event: CalendarSelectionEvent,
    onCalendarConfigured: (WidgetCalendarData) -> Unit
) {
    when (event) {
        is CalendarSelectionEvent.CalendarConfigured -> {
            onCalendarConfigured(event.calendarData)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: CalendarSelectionUiState,
    widgetSize: WidgetSize,
    calendarType: WidgetType.Calendar,
    onBackPressed: () -> Unit,
    onMonthChanged: (Int) -> Unit,
    onYearChanged: (Int) -> Unit,
    onDaySelected: (Int?) -> Unit,
    onBackgroundImageSelect: () -> Unit,
    onRemoveBackgroundImage: () -> Unit,
    onConfirm: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Setup Calendar",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Calendar Widget")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Widget Info
            item {
                WidgetInfoCard(
                    widgetSize = widgetSize,
                    calendarType = calendarType
                )
            }
            
            // Calendar Preview
            item {
                CalendarPreviewCard(
                    calendarData = uiState.calendarData,
                    widgetSize = widgetSize
                )
            }
            
            // Month/Year Selector
            item {
                MonthYearSelectorCard(
                    year = uiState.calendarData.year,
                    month = uiState.calendarData.month,
                    onMonthChanged = onMonthChanged,
                    onYearChanged = onYearChanged
                )
            }
            
            // Day Selection
            item {
                DaySelectionCard(
                    calendarData = uiState.calendarData,
                    onDaySelected = onDaySelected
                )
            }
            
            // Background Image
            item {
                BackgroundImageCard(
                    backgroundImagePath = uiState.calendarData.backgroundPath,
                    onBackgroundImageSelect = onBackgroundImageSelect,
                    onRemoveBackgroundImage = onRemoveBackgroundImage
                )
            }
        }
    }
}

@Composable
private fun WidgetInfoCard(
    widgetSize: WidgetSize,
    calendarType: WidgetType.Calendar
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Widget Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Size: ${widgetSize.name}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Type: ${calendarType.typeId}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CalendarPreviewCard(
    calendarData: WidgetCalendarData,
    widgetSize: WidgetSize
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Calendar preview based on widget size
            CalendarPreview(
                calendarData = calendarData,
                widgetSize = widgetSize
            )
        }
    }
}

@Composable
private fun CalendarPreview(
    calendarData: WidgetCalendarData,
    widgetSize: WidgetSize
) {
    val aspectRatio = when (widgetSize) {
        WidgetSize.SMALL -> 1f
        WidgetSize.MEDIUM -> 2f
        WidgetSize.LARGE -> 1f
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF34C759))
    ) {
        // Background image if available
        calendarData.backgroundPath?.let { imagePath ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imagePath)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }
        
        // Calendar content overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val monthName = CalendarUtils.getMonthName(calendarData.month, isShort = widgetSize == WidgetSize.SMALL)
            
            Text(
                text = "$monthName ${calendarData.year}",
                color = Color.White,
                fontSize = when (widgetSize) {
                    WidgetSize.SMALL -> 12.sp
                    WidgetSize.MEDIUM -> 14.sp
                    WidgetSize.LARGE -> 16.sp
                },
                fontWeight = FontWeight.Bold
            )
            
            if (widgetSize != WidgetSize.SMALL) {
                Spacer(modifier = Modifier.height(4.dp))
                
                // Simple calendar grid preview
                CalendarGridPreview(
                    calendarData = calendarData,
                    isSmall = widgetSize == WidgetSize.MEDIUM
                )
            } else {
                // Show today's date large for small widget
                calendarData.todayDay?.let { day ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = day.toString(),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarGridPreview(
    calendarData: WidgetCalendarData,
    isSmall: Boolean
) {
    val weeks = CalendarUtils.getCalendarWeeks(calendarData.year, calendarData.month)
    val maxWeeks = if (isSmall) 3 else weeks.size // Show fewer weeks for medium size
    
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        weeks.take(maxWeeks).forEach { week ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(if (isSmall) 12.dp else 16.dp)
                            .background(
                                when {
                                    day == calendarData.todayDay -> Color(0xFF007AFF)
                                    day == calendarData.selectedDay -> Color(0xFF34C759)
                                    day != null -> Color.White.copy(alpha = 0.3f)
                                    else -> Color.Transparent
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (day != null) {
                            Text(
                                text = day.toString(),
                                color = Color.White,
                                fontSize = if (isSmall) 6.sp else 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthYearSelectorCard(
    year: Int,
    month: Int,
    onMonthChanged: (Int) -> Unit,
    onYearChanged: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Date",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Month selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val (newYear, newMonth) = CalendarUtils.getPreviousMonth(year, month)
                        onYearChanged(newYear)
                        onMonthChanged(newMonth)
                    }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
                }
                
                Text(
                    text = "${CalendarUtils.getMonthName(month)} $year",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                IconButton(
                    onClick = {
                        val (newYear, newMonth) = CalendarUtils.getNextMonth(year, month)
                        onYearChanged(newYear)
                        onMonthChanged(newMonth)
                    }
                ) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
                }
            }
        }
    }
}

@Composable
private fun DaySelectionCard(
    calendarData: WidgetCalendarData,
    onDaySelected: (Int?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Selected Day (Optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onDaySelected(null) },
                    modifier = Modifier.weight(1f),
                    border = if (calendarData.selectedDay == null) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else null
                ) {
                    Text("None")
                }
                
                calendarData.todayDay?.let { today ->
                    OutlinedButton(
                        onClick = { onDaySelected(today) },
                        modifier = Modifier.weight(1f),
                        border = if (calendarData.selectedDay == today) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else null
                    ) {
                        Text("Today ($today)")
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundImageCard(
    backgroundImagePath: String?,
    onBackgroundImageSelect: () -> Unit,
    onRemoveBackgroundImage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Background Image (Optional)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (backgroundImagePath != null) {
                // Show current background image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(backgroundImagePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackgroundImageSelect,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change")
                    }
                    
                    OutlinedButton(
                        onClick = onRemoveBackgroundImage,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Remove")
                    }
                }
            } else {
                // Show add background button
                OutlinedButton(
                    onClick = onBackgroundImageSelect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Background Image")
                }
            }
        }
    }
}