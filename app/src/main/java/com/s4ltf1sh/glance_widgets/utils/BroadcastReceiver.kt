package com.s4ltf1sh.glance_widgets.utils

import android.content.BroadcastReceiver
import android.util.Log
import com.s4ltf1sh.glance_widgets.WidgetApplication.Companion.APP_TAG
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun BroadcastReceiver.goAsync(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    block: suspend CoroutineScope.() -> Unit,
) {
    val coroutineScope = CoroutineScope(SupervisorJob() + coroutineContext)
    val pendingResult = goAsync()

    coroutineScope.launch {
        try {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (t: Throwable) {
                Log.e(APP_TAG, "BroadcastReceiver execution failed", t)
            } finally {
                // Nothing can be in the `finally` block after this, as this throws a
                // `CancellationException`
                coroutineScope.cancel()
            }
        } finally {
            // This must be the last call, as the process may be killed after calling this.
            try {
                pendingResult?.finish()
            } catch (e: IllegalStateException) {
                // On some OEM devices, this may throw an error about "Broadcast already finished".
                // See b/257513022.
                Log.e(APP_TAG, "Error thrown when trying to finish broadcast", e)
            }
        }
    }
}