package com.s4ltf1sh.glance_widgets.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface WidgetType {
    val typeId: String

    @Serializable
    @SerialName("NONE")
    data object None : WidgetType {
        override val typeId = "NONE"
    }

    @Serializable
    @SerialName("PHOTO")
    data object Photo : WidgetType {
        override val typeId = "PHOTO"
    }

    @Serializable
    @SerialName("QUOTE")
    data object Quote : WidgetType {
        override val typeId = "QUOTE"
    }

    @Serializable
    sealed interface Clock : WidgetType {
        @Serializable
        sealed interface Digital : Clock {
            @Serializable
            @SerialName("CLOCK_DIGITAL_TYPE1")
            data object Type1 : Digital {
                override val typeId = "CLOCK_DIGITAL_TYPE1"
            }

            @Serializable
            @SerialName("CLOCK_DIGITAL_TYPE2")
            data object Type2 : Digital {
                override val typeId = "CLOCK_DIGITAL_TYPE2"
            }
        }

        @Serializable
        @SerialName("CLOCK_ANALOG")
        data object Analog : Clock {
            override val typeId = "CLOCK_ANALOG"
        }
    }

    @Serializable
    sealed interface Calendar : WidgetType {
        @Serializable
        @SerialName("CALENDAR_TYPE1")
        data object Type1 : Calendar {
            override val typeId = "CALENDAR_TYPE1"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE2")
        data object Type2 : Calendar {
            override val typeId = "CALENDAR_TYPE2"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE3")
        data object Type3 : Calendar {
            override val typeId = "CALENDAR_TYPE3"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE4")
        data object Type4 : Calendar {
            override val typeId = "CALENDAR_TYPE4"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE5")
        data object Type5 : Calendar {
            override val typeId = "CALENDAR_TYPE5"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE6")
        data object Type6 : Calendar {
            override val typeId = "CALENDAR_TYPE6"
        }
    }

    @Serializable
    sealed interface Weather : WidgetType {
        @Serializable
        @SerialName("WEATHER_TYPE1")
        data object Type1 : Weather {
            override val typeId = "WEATHER_TYPE1"
        }

        @Serializable
        @SerialName("WEATHER_TYPE2")
        data object Type2 : Weather {
            override val typeId = "WEATHER_TYPE2"
        }

        @Serializable
        @SerialName("WEATHER_TYPE3")
        data object Type3 : Weather {
            override val typeId = "WEATHER_TYPE3"
        }

        @Serializable
        @SerialName("WEATHER_TYPE4")
        data object Type4 : Weather {
            override val typeId = "WEATHER_TYPE4"
        }
    }

    companion object {
        // Registry pattern
        private val typeRegistry: Map<String, WidgetType> by lazy {
            buildMap {
                registerType(None)
                registerType(Photo)
                registerType(Quote)
                registerType(Clock.Digital.Type1)
                registerType(Clock.Digital.Type2)
                registerType(Clock.Analog)
                registerType(Calendar.Type1)
                registerType(Calendar.Type2)
                registerType(Calendar.Type3)
                registerType(Calendar.Type4)
                registerType(Calendar.Type5)
                registerType(Calendar.Type6)
                registerType(Weather.Type1)
                registerType(Weather.Type2)
                registerType(Weather.Type3)
                registerType(Weather.Type4)
            }
        }

        private fun MutableMap<String, WidgetType>.registerType(type: WidgetType) {
            put(type.typeId, type)
        }

        fun fromTypeId(typeId: String): WidgetType? = typeRegistry[typeId]

        fun getAllTypes(): List<WidgetType> = typeRegistry.values.toList()

        fun getAllMainTypes(): List<WidgetType> = listOf(
            None, Photo, Quote,
            Clock.Digital.Type1, Clock.Analog,
            Calendar.Type1,
            Weather.Type1
        )

        // Migration support
        fun fromLegacyEnum(legacyType: String): WidgetType = when (legacyType) {
            "WEATHER" -> Weather.Type1
            "CALENDAR" -> Calendar.Type1
            "PHOTO" -> Photo
            "QUOTE" -> Quote
            "NONE" -> None
            else -> None
        }
    }
}