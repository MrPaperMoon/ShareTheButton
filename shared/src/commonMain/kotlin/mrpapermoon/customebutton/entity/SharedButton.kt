package mrpapermoon.customebutton.entity

import androidx.compose.ui.graphics.Color

/**
 * Shared button model passed across repository and presentation boundaries.
 *
 * Describes button identity, appearance and action text. Its Compose [Color] dependency
 * is part of the current model, and persistence adapters encode it as ARGB Long.
 *
 * The same model is used for a freshly created button and one loaded by a backend.
 * Room ownership and invitation tokens are tracked separately. In particular, [id]
 * identifies the button throughout its lifetime and is not the token used to share it.
 *
 * @property id Button identifier.
 * @property color Background.
 * @property title List label.
 * @property actionText Action label.
 * @property notificationText Preview message.
 */
data class SharedButton(
    val id: String,
    val color: Color,
    val title: String,
    val actionText: String,
    val notificationText: String
) {
    val isStub: Boolean
        get() = id == NO_ID

    companion object {
        private const val NO_ID = "NO_ID"

        fun stub() = SharedButton(
            id = NO_ID,
            color = Color.Cyan,
            title = "",
            actionText = "",
            notificationText = "",
        )
    }
}
