package mrpapermoon.customebutton.ui.create

/** Maximum length of the title and action label, measured as Kotlin string length. */
const val BUTTON_LIMIT = 24

/** Maximum length of the notification text, measured as Kotlin string length. */
const val NOTIFICATION_LIMIT = 200

/**
 * Represents one snapshot of the creation wizard.
 *
 * Presentation state for step selection, enabled controls and the save result.
 * Draft fields and their validation live in [mrpapermoon.customebutton.ButtonUiState].
 * [createdButtonId] identifies a successfully saved button.
 *
 * @property step Active step.
 * @property enabled Wizard controls enabled.
 * @property createdButtonId Saved button ID.
 * @property error Save failure.
 */
data class CreateButtonViewState(
    val step: CreateButtonStep = CreateButtonStep.entries.first(),
    val enabled: Boolean = true,
    val createdButtonId: String? = null,
    val error: CreateButtonEffect.Error? = null
) {
    val canGoBack: Boolean
        get() = step.ordinal != 0

    val canGoNext: Boolean
        get() = step.ordinal != CreateButtonStep.entries.lastIndex
}
