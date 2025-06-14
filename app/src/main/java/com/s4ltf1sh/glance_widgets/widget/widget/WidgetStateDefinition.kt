package com.s4ltf1sh.glance_widgets.widget.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.s4ltf1sh.glance_widgets.model.WidgetState
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object WidgetStateDefinition : GlanceStateDefinition<WidgetState> {
    private const val DATA_STORE_FILENAME = "widget_state"

    private val Context.datastore by dataStore(DATA_STORE_FILENAME, WidgetStateSerializer)
    override suspend fun getDataStore(context: Context, fileKey: String): DataStore<WidgetState> {
        return context.datastore
    }

    override fun getLocation(
        context: Context,
        fileKey: String
    ): File {
        return context.dataStoreFile(DATA_STORE_FILENAME)
    }

    private object WidgetStateSerializer : Serializer<WidgetState> {
        override val defaultValue = WidgetState.Loading

        override suspend fun readFrom(input: InputStream): WidgetState = try {
            Json.decodeFromString(
                WidgetState.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (exception: SerializationException) {
            throw CorruptionException("Could not read data: ${exception.message}")
        }

        override suspend fun writeTo(t: WidgetState, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(WidgetState.serializer(), t).encodeToByteArray()
                )
            }
        }
    }
}