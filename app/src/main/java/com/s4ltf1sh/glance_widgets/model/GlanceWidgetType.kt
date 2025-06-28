package com.s4ltf1sh.glance_widgets.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface GlanceWidgetType {
    val typeId: String

    @Serializable
    @SerialName("NONE")
    data object None : GlanceWidgetType {
        override val typeId = "NONE"
    }

    @Serializable
    @SerialName("PHOTO")
    data object Photo : GlanceWidgetType {
        override val typeId = "PHOTO"
    }

    @Serializable
    @SerialName("QUOTE")
    data object Quote : GlanceWidgetType {
        override val typeId = "QUOTE"
    }

    @Serializable
    sealed interface Clock : GlanceWidgetType {
        @Serializable
        sealed interface Digital : Clock {
            @Serializable
            @SerialName("CLOCK_DIGITAL_TYPE1")
            data object Type1Glance : Digital {
                override val typeId = "CLOCK_DIGITAL_TYPE1"
            }

            @Serializable
            @SerialName("CLOCK_DIGITAL_TYPE2")
            data object Type2Glance : Digital {
                override val typeId = "CLOCK_DIGITAL_TYPE2"
            }
        }

        @Serializable
        sealed interface Analog : Clock {
            @Serializable
            @SerialName("CLOCK_ANALOG_TYPE1")
            data object Type1Glance : Analog {
                override val typeId = "CLOCK_ANALOG_TYPE1"
            }

            @Serializable
            @SerialName("CLOCK_ANALOG_TYPE2")
            data object Type2Glance : Analog {
                override val typeId = "CLOCK_ANALOG_TYPE2"
            }
        }
    }

    @Serializable
    sealed interface Calendar : GlanceWidgetType {
        @Serializable
        @SerialName("CALENDAR_TYPE1")
        data object Type1Glance : Calendar {
            override val typeId = "CALENDAR_TYPE1"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE2")
        data object Type2Glance : Calendar {
            override val typeId = "CALENDAR_TYPE2"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE3")
        data object Type3Glance : Calendar {
            override val typeId = "CALENDAR_TYPE3"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE4")
        data object Type4Glance : Calendar {
            override val typeId = "CALENDAR_TYPE4"
        }

        @Serializable
        @SerialName("CALENDAR_TYPE5")
        data object Type5Glance : Calendar {
            override val typeId = "CALENDAR_TYPE5"
        }
    }

    @Serializable
    sealed interface Weather : GlanceWidgetType {
        @Serializable
        @SerialName("WEATHER_TYPE1")
        data object Type1Glance : Weather {
            override val typeId = "WEATHER_TYPE1"
        }

        @Serializable
        @SerialName("WEATHER_TYPE2")
        data object Type2Glance : Weather {
            override val typeId = "WEATHER_TYPE2"
        }

        @Serializable
        @SerialName("WEATHER_TYPE3")
        data object Type3Glance : Weather {
            override val typeId = "WEATHER_TYPE3"
        }

        @Serializable
        @SerialName("WEATHER_TYPE4")
        data object Type4Glance : Weather {
            override val typeId = "WEATHER_TYPE4"
        }
    }

    companion object {
        // Registry pattern
        private val typeRegistry: Map<String, GlanceWidgetType> by lazy {
            buildMap {
                registerType(None)
                registerType(Photo)
                registerType(Quote)
                registerType(Clock.Digital.Type1Glance)
                registerType(Clock.Digital.Type2Glance)
                registerType(Clock.Analog.Type1Glance)
                registerType(Clock.Analog.Type2Glance)
                registerType(Calendar.Type1Glance)
                registerType(Calendar.Type2Glance)
                registerType(Calendar.Type3Glance)
                registerType(Calendar.Type4Glance)
                registerType(Calendar.Type5Glance)
                registerType(Weather.Type1Glance)
                registerType(Weather.Type2Glance)
                registerType(Weather.Type3Glance)
                registerType(Weather.Type4Glance)
            }
        }

        private fun MutableMap<String, GlanceWidgetType>.registerType(type: GlanceWidgetType) {
            put(type.typeId, type)
        }

        fun fromTypeId(typeId: String): GlanceWidgetType? = typeRegistry[typeId]

        fun getAllTypes(): List<GlanceWidgetType> = typeRegistry.values.toList()

        fun getAllMainTypes(): List<GlanceWidgetType> = listOf(
            None, Photo, Quote,
            Clock.Digital.Type1Glance,
            Clock.Digital.Type2Glance,
            Clock.Analog.Type1Glance,
            Clock.Analog.Type2Glance,
            Calendar.Type1Glance,
            Weather.Type1Glance
        )

        // Migration support
        fun fromLegacyEnum(legacyType: String): GlanceWidgetType = when (legacyType) {
            "WEATHER" -> Weather.Type1Glance
            "CALENDAR" -> Calendar.Type1Glance
            "PHOTO" -> Photo
            "QUOTE" -> Quote
            "NONE" -> None
            else -> None
        }
    }
}