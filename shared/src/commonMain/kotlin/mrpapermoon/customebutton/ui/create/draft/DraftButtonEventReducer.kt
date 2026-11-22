package mrpapermoon.customebutton.ui.create.draft

import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.ui.EventReducer

/**
 * Presentation reducer updating the draft fields. Text input currently replaces spaces
 * with underscores before storing the edited value.
 */
class DraftButtonEventReducer : EventReducer<DraftButtonEvent, ButtonUiState> {
    override fun ButtonUiState.reduce(event: DraftButtonEvent): ButtonUiState {
        return when (event) {
            is DraftButtonEvent.ActionChanged -> copy(actionText = event.text.trimButtonText())
            is DraftButtonEvent.ColorChanged -> copy(color = event.color)
            is DraftButtonEvent.NotificationChanged -> copy(notificationText = event.text.trimButtonText())
            is DraftButtonEvent.TitleChanged -> copy(title = event.text.trimButtonText())
        }
    }

    private fun String.trimButtonText() = replace(' ', '_')
}
