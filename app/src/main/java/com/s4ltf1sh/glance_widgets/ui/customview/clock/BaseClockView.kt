package com.example.customclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.View
import java.util.Calendar
import java.util.TimeZone

/**
 * Base clock view that handles time updates like TextClock
 * This is an abstract class - subclasses must implement onDraw
 */
abstract class BaseClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Time format properties
    var format12Hour: CharSequence? = null
        set(value) {
            field = value
            chooseFormat()
            onTimeChanged()
        }

    var format24Hour: CharSequence? = null
        set(value) {
            field = value
            chooseFormat()
            onTimeChanged()
        }

    // Internal properties
    protected var mFormat: CharSequence = ""
    protected var mHasSeconds: Boolean = false
    private var mAttached: Boolean = false
    private var mTimeZone: String? = null
    protected var mTime: Calendar = Calendar.getInstance()

    // Current time string - subclasses can access this
    protected var mTimeString: String = ""

    // Broadcast receiver for time changes
    private val mIntentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_TIMEZONE_CHANGED -> {
                    val tz = intent.getStringExtra("time-zone")
                    createTime(tz)
                }
            }
            onTimeChanged()
        }
    }

    // Ticker for updating time with seconds
    private val mTicker = object : Runnable {
        override fun run() {
            onTimeChanged()

            val now = SystemClock.uptimeMillis()
            val next = now + (1000 - now % 1000)

            handler?.postAtTime(this, next)
        }
    }

    // Format change observer
    private val mFormatChangeObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            chooseFormat()
            onTimeChanged()
        }
    }

    init {
        // Set default formats
        if (format12Hour == null) {
            format12Hour = "h:mm a"
        }
        if (format24Hour == null) {
            format24Hour = "HH:mm"
        }

        createTime(mTimeZone)
        chooseFormat(false)
    }

    private fun createTime(tz: String?) {
        mTime = if (tz != null) {
            Calendar.getInstance(TimeZone.getTimeZone(tz))
        } else {
            Calendar.getInstance()
        }
    }

    private fun chooseFormat(handleTicker: Boolean = true) {
        val format24Requested = is24HourModeEnabled()

        mFormat = when {
            format24Requested && format24Hour != null -> format24Hour!!
            !format24Requested && format12Hour != null -> format12Hour!!
            format24Requested -> "HH:mm"
            else -> "h:mm a"
        }

        val hadSeconds = mHasSeconds
        mHasSeconds = checkHasSeconds(mFormat)

        if (handleTicker && mAttached && hadSeconds != mHasSeconds) {
            if (hadSeconds) {
                handler?.removeCallbacks(mTicker)
            } else {
                mTicker.run()
            }
        }
    }

    /**
     * Check if format string contains seconds
     */
    private fun checkHasSeconds(format: CharSequence): Boolean {
        val formatString = format.toString()
        return formatString.contains('s') || formatString.contains('S')
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if (!mAttached) {
            mAttached = true

            registerReceiver()
            registerObserver()

            createTime(mTimeZone)

            if (mHasSeconds) {
                mTicker.run()
            } else {
                onTimeChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (mAttached) {
            unregisterReceiver()
            unregisterObserver()

            handler?.removeCallbacks(mTicker)

            mAttached = false
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }

        context.registerReceiver(mIntentReceiver, filter)
    }

    private fun unregisterReceiver() {
        context.unregisterReceiver(mIntentReceiver)
    }

    private fun registerObserver() {
        val resolver = context.contentResolver
        val uri = Settings.System.getUriFor(Settings.System.TIME_12_24)
        resolver.registerContentObserver(uri, true, mFormatChangeObserver)
    }

    private fun unregisterObserver() {
        val resolver = context.contentResolver
        resolver.unregisterContentObserver(mFormatChangeObserver)
    }

    protected open fun onTimeChanged() {
        mTime.timeInMillis = System.currentTimeMillis()
        mTimeString = DateFormat.format(mFormat, mTime).toString()
        invalidate()
    }

    /**
     * Set time zone for the clock
     */
    fun setTimeZone(tz: String?) {
        mTimeZone = tz
        createTime(tz)
        onTimeChanged()
    }

    /**
     * Get current time zone
     */
    fun getTimeZone(): String? = mTimeZone

    /**
     * Refresh the time display
     */
    fun refreshTime() {
        onTimeChanged()
    }

    /**
     * Check if 24-hour mode is enabled
     */
    fun is24HourModeEnabled(): Boolean {
        return DateFormat.is24HourFormat(context)
    }

    /**
     * Get current format being used
     */
    fun getFormat(): CharSequence = mFormat
}