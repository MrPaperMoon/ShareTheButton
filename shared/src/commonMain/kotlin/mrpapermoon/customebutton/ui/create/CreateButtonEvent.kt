package mrpapermoon.customebutton.ui.create

import mrpapermoon.customebutton.ui.Event

sealed class CreateButtonEvent : Event {
    /** Selects the wizard [step] directly. */
    data class SelectStep(val step: CreateButtonStep) : CreateButtonEvent()

    /** Requests the next wizard step. */
    data object Next : CreateButtonEvent()

    /** Requests the previous wizard step. */
    data object Previous : CreateButtonEvent()

    /** Requests local persistence of a valid draft from the confirmation step. */
    data object Save : CreateButtonEvent()

    /** Clears the current save error. */
    data object DismissError : CreateButtonEvent()
}
