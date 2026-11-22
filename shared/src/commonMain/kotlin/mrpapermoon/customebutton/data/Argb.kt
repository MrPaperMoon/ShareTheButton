package mrpapermoon.customebutton.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/** Encodes a Compose color as a non-negative 32-bit sRGB ARGB value in a [Long]. */
internal fun Color.toArgbLong(): Long = toArgb().toUInt().toLong()
