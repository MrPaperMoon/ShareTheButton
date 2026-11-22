package mrpapermoon.customebutton

import androidx.compose.ui.graphics.Color
import mrpapermoon.customebutton.ui.create.BUTTON_LIMIT
import mrpapermoon.customebutton.ui.create.NOTIFICATION_LIMIT

/**
 * Holds the fields being edited before a button exists in storage.
 *
 * The wizard starts with an initial color and empty texts. The reducer replaces
 * these values as the user edits them, while [mrpapermoon.customebutton.ui.create.CreateButtonViewState]
 * owns step selection and save status. [dataIsValid] validates the draft fields. The ViewModel assigns an ID when saving the draft.
 *
 * @property color Background.
 * @property title List label.
 * @property actionText Action label.
 * @property notificationText Draft message.
 */
data class ButtonUiState(
    val color: Color = Color(0xFFF4A7C1),
    val title: String = "",
    val actionText: String = "",
    val notificationText: String = "",
) {

    val dataIsValid: Boolean
        get() = title.isNotBlank() && title.length <= BUTTON_LIMIT &&
                actionText.isNotBlank() && actionText.length <= BUTTON_LIMIT &&
                notificationText.isNotBlank() && notificationText.length <= NOTIFICATION_LIMIT
}
