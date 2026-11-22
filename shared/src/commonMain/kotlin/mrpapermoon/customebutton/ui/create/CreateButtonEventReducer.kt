package mrpapermoon.customebutton.ui.create

import mrpapermoon.customebutton.logging.logNonFatal
import mrpapermoon.customebutton.ui.EventReducer

/**
 * Presentation reducer for wizard step changes and dismissal of save feedback.
 * Draft edits are handled by the separate draft reducer.
 */
class CreateButtonEventReducer : EventReducer<CreateButtonEvent, CreateButtonViewState> {
    override fun CreateButtonViewState.reduce(event: CreateButtonEvent): CreateButtonViewState {
        return when (event) {
            is CreateButtonEvent.SelectStep -> {
                copy(step = event.step)
            }
            CreateButtonEvent.Next -> {
                if (!canGoNext) {
                    logNonFatal { "Calling Next on last step" }
                    return this
                }
                copy(step = CreateButtonStep.entries[step.ordinal + 1])
            }
            CreateButtonEvent.Previous -> {
                if (!canGoBack) {
                    logNonFatal { "Calling Previous on first step" }
                    return this
                }
                copy(step = CreateButtonStep.entries[step.ordinal - 1])
            }
            CreateButtonEvent.DismissError -> {
                copy(error = null)
            }
            else -> {
                logNonFatal { "Unexpecting event reduce call $event" }
                this
            }
        }
    }
}
