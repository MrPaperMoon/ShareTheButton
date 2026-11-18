package mrpapermoon.customebutton.ui.create

import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.ui.common.ViewError

const val BUTTON_LIMIT = 24
const val NOTIFICATION_LIMIT = 200

data class CreateButtonViewState(
    val step: CreateButtonStep = CreateButtonStep.Color,
    val draft: ButtonUiState = ButtonUiState(ButtonColor.Pink, "", "", ""),
    val saving: Boolean = false,
    val completed: Boolean = false,
    val error: ViewError? = null
) {
    val canGoNext: Boolean
        get() = !saving && !completed && step != CreateButtonStep.Confirm &&
            (step != CreateButtonStep.Text || canSave)

    val canGoBack: Boolean
        get() = !saving && !completed && step != CreateButtonStep.Color

    val canSave: Boolean
        get() = draft.title.isNotBlank() && draft.title.length <= BUTTON_LIMIT &&
            draft.actionText.isNotBlank() && draft.actionText.length <= BUTTON_LIMIT &&
            draft.notificationText.isNotBlank() && draft.notificationText.length <= NOTIFICATION_LIMIT && !completed
}

fun CreateButtonViewState.reduce(event: CreateButtonEvent): CreateButtonViewState {
    if (saving || completed) return this
    return when (event) {
        is CreateButtonEvent.ColorChanged -> copy(draft = draft.copy(color = event.color))
        is CreateButtonEvent.TitleChanged -> copy(draft = draft.copy(title = event.text))
        is CreateButtonEvent.ActionChanged -> copy(draft = draft.copy(actionText = event.text))
        is CreateButtonEvent.NotificationChanged -> copy(draft = draft.copy(notificationText = event.text))
        is CreateButtonEvent.SelectStep -> copy(step = event.step)
        CreateButtonEvent.Next -> if (canGoNext) copy(step = CreateButtonStep.entries[step.ordinal + 1]) else this
        CreateButtonEvent.Previous -> if (canGoBack) copy(step = CreateButtonStep.entries[step.ordinal - 1]) else this
        CreateButtonEvent.Cancel -> copy(completed = true)
        CreateButtonEvent.DismissError -> copy(error = null)
        CreateButtonEvent.Save -> this
    }
}
