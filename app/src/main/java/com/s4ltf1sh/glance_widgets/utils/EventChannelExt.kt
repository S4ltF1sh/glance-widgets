package com.s4ltf1sh.glance_widgets.utils

import android.util.Log
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.s4ltf1sh.glance_widgets.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.io.Closeable
import javax.inject.Inject
import kotlin.collections.plus
import kotlin.coroutines.ContinuationInterceptor

interface HasEventFlow<E> {
    val eventFlow: Flow<E>
}

suspend fun debugCheckImmediateMainDispatcher() {
    if (BuildConfig.DEBUG) {
        val interceptor = currentCoroutineContext()[ContinuationInterceptor]
        Log.d(
            "###",
            "debugCheckImmediateMainDispatcher: $interceptor, ${Dispatchers.Main.immediate}, ${Dispatchers.Main}"
        )

        check(interceptor === Dispatchers.Main.immediate) {
            "Expected ContinuationInterceptor to be Dispatchers.Main.immediate but was $interceptor"
        }
    }
}

@MainThread
class EventChannel<E>
@Inject
constructor() : Closeable, HasEventFlow<E> {
    // Buffer latest event only
    // NOTE: Do not use Channel.UNLIMITED, it can lead to memory issues if events accumulate faster than they're consumed
    private val _eventChannel =
        Channel<E>(Channel.BUFFERED, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val _pendingEvents = MutableStateFlow<List<E>>(emptyList())

    // Collect pending events when collector becomes active
    @OptIn(ExperimentalCoroutinesApi::class)
    override val eventFlow: Flow<E> = merge(
        _eventChannel.receiveAsFlow(),
        _pendingEvents.flatMapLatest { events ->
            flow {
                events.forEach { emit(it) }
            }
        }
    )

    init {
        Log.d("EVENT_CHANNEL", "[EventChannel] created: hashCode=${System.identityHashCode(this)}")
    }

    /**
     * Must be called in Dispatchers.Main.immediate, otherwise it will throw an exception.
     * If you want to send an event from other Dispatcher,
     * use `withContext(Dispatchers.Main.immediate) { eventChannel.send(event) }`
     */
    @MainThread
    suspend fun send(event: E) {
        debugCheckImmediateMainDispatcher()

        _pendingEvents.update { it + event }

        _eventChannel.trySend(event)
            .onFailure { throwable ->
                throwable?.let { Log.e("EVENT_CHANNEL", it.message.toString()) }
                Log.e("EVENT_CHANNEL",
                    "[EventChannel] Failed to send event: $event, hashCode=${
                        System.identityHashCode(
                            this
                        )
                    }"
                )
            }
            .onSuccess {
                _pendingEvents.update { it - event }
                Log.d("EVENT_CHANNEL",
                    "[EventChannel] Sent event: $event, hashCode=${
                        System.identityHashCode(
                            this
                        )
                    }"
                )
            }
            .getOrThrow()
    }

    override fun close() {
        Log.d("EVENT_CHANNEL","[EventChannel] closed: hashCode=${System.identityHashCode(this)}")
        _eventChannel.close()
    }
}

@Composable
fun <T : Any> SingleEventEffect(
    sideEffectFlow: Flow<T>,
    lifeCycleState: Lifecycle.State = Lifecycle.State.STARTED,
    key: Any? = sideEffectFlow,
    onError: (Throwable) -> Unit = { throw it },
    collector: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key) {
        lifecycleOwner.repeatOnLifecycle(lifeCycleState) {
            try {
                sideEffectFlow.collect(collector)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}