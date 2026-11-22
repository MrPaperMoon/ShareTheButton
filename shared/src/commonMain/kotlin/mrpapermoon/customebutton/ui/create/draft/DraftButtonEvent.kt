package mrpapermoon.customebutton.ui.create.draft

import androidx.compose.ui.graphics.Color
import mrpapermoon.customebutton.ui.Event

/**
 * Edits to the unsaved button appearance and text fields.
 */
sealed class DraftButtonEvent : Event {
    /** Updates the draft background [color]. */
    data class ColorChanged(val color: Color) : DraftButtonEvent()

    /** Replaces the draft title with [text]. */
    data class TitleChanged(val text: String) : DraftButtonEvent()

    /** Replaces the draft action label with [text]. */
    data class ActionChanged(val text: String) : DraftButtonEvent()

    /** Replaces the draft notification message with [text]. */
    data class NotificationChanged(val text: String) : DraftButtonEvent()
}
