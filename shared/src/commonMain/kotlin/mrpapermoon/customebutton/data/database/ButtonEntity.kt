package mrpapermoon.customebutton.data.database

import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import mrpapermoon.customebutton.entity.SharedButton

/**
 * Represents a local button row in Room.
 *
 * The row keeps ownership and the persisted invitation token alongside the button's
 * visual data. [toButton] maps only the display data to the shared model.
 *
 * @property id Primary key.
 * @property color Packed ARGB.
 * @property title List label.
 * @property actionText Action label.
 * @property notificationText Preview message.
 * @property isOwned Local ownership.
 * @property shareToken Optional invitation token.
 */
@Entity(tableName = "buttons")
data class ButtonEntity(
    @PrimaryKey val id: String,
    @ColorInt val color: Int,
    val title: String,
    val actionText: String,
    val notificationText: String,
    val isOwned: Boolean = true,
    val shareToken: String? = null
) {
    /** Returns the button model, decoding the stored ARGB value into a Compose color. */
    fun toButton(): SharedButton = SharedButton(id, Color(color), title, actionText, notificationText)
}

/**
 * Converts a button into a row for the local database.
 *
 * @receiver Button whose color is encoded as a packed ARGB Int.
 * @param isOwned Whether the row represents an owned button.
 * @param shareToken Invitation token to store alongside the row.
 * @return The corresponding Room row.
 */
fun SharedButton.toEntity(isOwned: Boolean = true, shareToken: String? = null): ButtonEntity =
    ButtonEntity(id, color.toArgb(), title, actionText, notificationText, isOwned, shareToken)
