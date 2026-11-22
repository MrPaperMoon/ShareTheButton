package mrpapermoon.customebutton.ui.welcome

import androidx.compose.runtime.Immutable

/**
 * Snapshot of local guest entry.
 *
 * The state starts idle and reports progress while preferences are being written.
 * It records completion once the session has been saved.
 *
 * @property entered Session saved.
 * @property progress Write in progress.
 * @property enabledEnterModes Collection of enabled sign-in methods.
 */
@Immutable
data class WelcomeViewState(
    val entered: Boolean = false,
    val progress: Boolean = false,
    val enabledEnterModes: Set<WelcomeEnterMode> = setOf(WelcomeEnterMode.GUEST),
)
