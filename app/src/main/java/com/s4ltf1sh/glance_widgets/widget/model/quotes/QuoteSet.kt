package com.s4ltf1sh.glance_widgets.widget.model.quotes

import com.s4ltf1sh.glance_widgets.db.quote.QuoteEntity

data class QuoteSet(
    val setId: String,
    val setName: String,
    val smallQuote: QuoteEntity,
    val mediumQuote: QuoteEntity,
    val largeQuote: QuoteEntity
)