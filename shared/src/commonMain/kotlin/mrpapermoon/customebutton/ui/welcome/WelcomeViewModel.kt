package mrpapermoon.customebutton.ui.welcome

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.repository.SessionRepository

interface WelcomeViewModel : WelcomeViewModelContract {
    val sessionRepository: SessionRepository

    override val viewState: MutableStateFlow<WelcomeViewState>

    override fun onEvent(event: WelcomeEvent) {
        when (event) {
            WelcomeEvent.Skip -> skip()
        }
    }

    private fun skip() {
        if (viewState.value.progress || viewState.value.entered) throw IllegalStateException("Skip action processing already")
        viewState.update { it.copy(progress = true) }
        coroutineScope.launch {
            sessionRepository.enterAsGuest()
            viewState.update { it.copy(progress = false, entered = true) }
        }
    }
}
