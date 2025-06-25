package com.s4ltf1sh.glance_widgets.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.s4ltf1sh.glance_widgets.db.calendar.CalendarEntity
import com.s4ltf1sh.glance_widgets.model.WidgetSize
import com.s4ltf1sh.glance_widgets.model.WidgetType
import com.s4ltf1sh.glance_widgets.widget.component.WidgetImage

@Composable
fun CalendarSelectionScreen(
    widgetId: Int,
    widgetSize: WidgetSize,
    calendarType: WidgetType.Calendar,
    onBackPressed: () -> Unit,
    onCalendarSelected: (CalendarEntity) -> Unit
) {
    val viewModel: CalendarSelectionViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCalendars(widgetSize)
    }

    Content(
        uiState = uiState,
        widgetSize = widgetSize,
        calendarType = calendarType,
        onBackPressed = onBackPressed,
        onItemClicked = onCalendarSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: CalendarSelectionUiState,
    widgetSize: WidgetSize,
    calendarType: WidgetType.Calendar,
    onBackPressed: () -> Unit,
    onItemClicked: (CalendarEntity) -> Unit
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                uiState.calendars,
                key = { it.id }
            ) { calendar ->
                CalendarItem(
                    calendar = calendar,
                    widgetSize = widgetSize,
                    widgetType = calendarType,
                    onClick = {
                        onItemClicked(calendar)
                    }
                )
            }
        }
    }
}

@Composable
private fun CalendarItem(
    calendar: CalendarEntity,
    widgetSize: WidgetSize,
    widgetType: WidgetType.Calendar,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(getAspectRatio(widgetSize))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            WidgetImage(
                image = calendar.backgroundUrl,
                modifier = Modifier.fillMaxSize()
            )

            // Set name overlay
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.small
            ) {
                Column {
                    Text(
                        text = calendar.id.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = widgetType.typeId,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private fun getGridCellSize(widgetSize: WidgetSize): Dp = when (widgetSize) {
    WidgetSize.SMALL -> 120.dp
    WidgetSize.MEDIUM -> 160.dp
    WidgetSize.LARGE -> 200.dp
}

private fun getAspectRatio(widgetSize: WidgetSize): Float = when (widgetSize) {
    WidgetSize.SMALL -> 1f
    WidgetSize.MEDIUM -> 2f
    WidgetSize.LARGE -> 1f
}