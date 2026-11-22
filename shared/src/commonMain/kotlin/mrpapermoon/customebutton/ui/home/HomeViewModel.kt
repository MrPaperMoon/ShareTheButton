package mrpapermoon.customebutton.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import mrpapermoon.customebutton.repository.ButtonRepository

/**
 * Presentation model mapping repository button updates to [HomeViewState].
 *
 * The model starts observing [ButtonRepository.buttons] when created and remains subscribed
 * until it is cleared. The initial state contains an empty list until Room emits the first
 * snapshot. Collection failures propagate to the ViewModel coroutine and leave the last
 * emitted state available.
 *
 * @param repository Local button source.
 * @property viewState Current list state.
 */
@Inject
class HomeViewModel(repository: ButtonRepository) : ViewModel() {
    val viewState = repository.buttons
        .map { HomeViewState(buttons = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HomeViewState())
}
