package mrpapermoon.customebutton.ui.create

import mrpapermoon.customebutton.entity.ButtonColor

sealed class CreateButtonEvent {
    data class SelectStep(val step: CreateButtonStep) : CreateButtonEvent()
    data object Next : CreateButtonEvent()
    data object Previous : CreateButtonEvent()
    data class ColorChanged(val color: ButtonColor) : CreateButtonEvent()
    data class TitleChanged(val text: String) : CreateButtonEvent()
    data class ActionChanged(val text: String) : CreateButtonEvent()
    data class NotificationChanged(val text: String) : CreateButtonEvent()
    data object Save : CreateButtonEvent()
    data object Cancel : CreateButtonEvent()
    data object DismissError : CreateButtonEvent()
}
