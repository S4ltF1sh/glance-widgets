package com.s4ltf1sh.glance_widgets.widget.core

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.GlanceStateDefinition
import com.s4ltf1sh.glance_widgets.db.GlanceWidgetEntity
import com.s4ltf1sh.glance_widgets.model.GlanceWidget
import com.s4ltf1sh.glance_widgets.model.GlanceWidgetSize
import com.s4ltf1sh.glance_widgets.utils.updateWidgetUI
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

/**
 * Widget state sealed class to manage different widget states
 */
@Serializable
sealed interface AppWidgetState {
    @Serializable
    data object Init : AppWidgetState

    @Serializable
    data object Empty : AppWidgetState

    @Serializable
    data class Success(val glanceWidget: GlanceWidget) : AppWidgetState

    @Serializable
    data class Error(val message: String, val throwable: String? = null) : AppWidgetState
}

/**
 * State definition for managing widget state by widget ID
 * Each widget has its own DataStore file based on widget ID
 */
object BaseWidgetStateDefinition : GlanceStateDefinition<AppWidgetState> {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override suspend fun getDataStore(
        context: Context,
        fileKey: String
    ) = DataStoreFactory.create(
        serializer = WidgetStateSerializer,
        produceFile = {
            getLocation(context, fileKey)
        }
    )

    override fun getLocation(
        context: Context,
        fileKey: String
    ) = context.dataStoreFile("widget_state_$fileKey")

    suspend fun getState(context: Context, glanceId: GlanceId): AppWidgetState {
        return try {
            val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
            getDataStore(context, widgetId.toString()).data.first()
        } catch (e: Exception) {
            AppWidgetState.Init
        }
    }

    suspend fun updateState(
        context: Context,
        glanceId: GlanceId,
        updateBlock: suspend (AppWidgetState) -> AppWidgetState
    ) {
        try {
            val widgetId = GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
            getDataStore(context, widgetId.toString()).updateData { currentState ->
                updateBlock(currentState)
            }
        } catch (e: Exception) {
            android.util.Log.e("BaseWidgetStateDefinition", "Error updating state", e)
        }
    }

    /**
     * Serializer for WidgetState using JSON
     */
    private object WidgetStateSerializer : Serializer<AppWidgetState> {
        override val defaultValue: AppWidgetState = AppWidgetState.Init

        override suspend fun readFrom(input: InputStream): AppWidgetState = try {
            Json.decodeFromString(
                AppWidgetState.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read widget state: ${exception.message}")
        }

        override suspend fun writeTo(t: AppWidgetState, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(AppWidgetState.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}

/**
 * Helper function to update widget state to Success
 */
suspend fun Context.setWidgetSuccess(
    glanceId: GlanceId,
    glanceWidgetSize: GlanceWidgetSize,
    widget: GlanceWidgetEntity
) {
    updateAppWidgetState(
        context = this,
        definition = BaseWidgetStateDefinition,
        glanceId = glanceId
    ) {
        AppWidgetState.Success(widget.toWidget())
    }
    updateWidgetUI(glanceId, glanceWidgetSize)
}

/**
 * Helper function to update widget state to Error
 */
suspend fun Context.setWidgetError(
    glanceId: GlanceId,
    glanceWidgetSize: GlanceWidgetSize,
    message: String,
    throwable: Throwable? = null
) {
    updateAppWidgetState(
        context = this,
        definition = BaseWidgetStateDefinition,
        glanceId = glanceId
    ) {
        AppWidgetState.Error(message, throwable?.message)
    }

    updateWidgetUI(glanceId, glanceWidgetSize)
}

/**
 * Helper function to set widget state to Empty
 */
suspend fun Context.setWidgetEmpty(glanceId: GlanceId, glanceWidgetSize: GlanceWidgetSize) {
    updateAppWidgetState(
        context = this,
        definition = BaseWidgetStateDefinition,
        glanceId = glanceId
    ) {
        AppWidgetState.Empty
    }
    updateWidgetUI(glanceId, glanceWidgetSize)
}

/**
 * Helper function to refresh widget data
 */
suspend fun Context.refreshWidget(glanceId: GlanceId, glanceWidgetSize: GlanceWidgetSize) {
    updateAppWidgetState(
        context = this,
        definition = BaseWidgetStateDefinition,
        glanceId = glanceId
    ) {
        AppWidgetState.Init
    }
    updateWidgetUI(glanceId, glanceWidgetSize)
}

/**
 * Helper function to get widget ID from GlanceId
 */
suspend fun getWidgetId(context: Context, glanceId: GlanceId): Int {
    return GlanceAppWidgetManager(context).getAppWidgetId(glanceId)
}