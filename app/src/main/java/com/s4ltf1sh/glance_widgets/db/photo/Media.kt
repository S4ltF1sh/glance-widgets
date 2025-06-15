package com.s4ltf1sh.glance_widgets.db.photo

import android.net.Uri

data class Media(
    val uri: Uri,
    val path: String,
    val name: String,
    val size: Long,
    val mimeType: String,
)