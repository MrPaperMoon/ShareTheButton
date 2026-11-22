package mrpapermoon.customebutton.ui.home

import androidx.compose.runtime.Immutable
import mrpapermoon.customebutton.entity.SharedButton

/**
 * Snapshot rendered by the local button list.
 *
 * An empty list provides the empty-state model until buttons are available. The
 * optional error is reserved for failures reported to the list.
 *
 * @property buttons Local buttons.
 */
@Immutable
data class HomeViewState(
    val buttons: List<SharedButton> = emptyList(),
)
