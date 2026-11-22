package mrpapermoon.customebutton.ui.welcome

import androidx.datastore.core.IOException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mrpapermoon.customebutton.logging.logNonFatal
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.ui.Event
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.stickToEvent

/**
 * Presentation model handling the welcome screen guest-entry event through [SessionRepository].
 *
 * Routes sign-in events by [WelcomeEnterMode] and exposes progress and successful
 * guest entry to the screen. The current enabled mode is guest entry. OAuth branches
 * are reserved for future implementations.
 *
 * @property viewState Observable guest entry progress and completion.
 * @param sessionRepository Repository used to save guest entry.
 */
@Inject
class WelcomeViewModel(
    private val sessionRepository: SessionRepository,
) : ViewModel(), EventHandler {

    private val loginMutex = Mutex()

    val viewState: StateFlow<WelcomeViewState>
        field = MutableStateFlow(WelcomeViewState())

    override fun onEvent(event: Event) {
        stickToEvent<WelcomeEvent>(event)
        super.onEvent(event)

        when (event) {
            is WelcomeEvent.SignIn -> {
                viewModelScope.launch {
                    loginMutex.withLock {
                        viewState.update { it.copy(progress = true) }

                        when (event.enterMode) {
                            WelcomeEnterMode.APPLE_OAUTH -> TODO()
                            WelcomeEnterMode.GOOGLE_OAUTH -> TODO()
                            WelcomeEnterMode.GUEST -> guestLogin(event)
                        }
                    }
                }
            }
        }
    }

    private suspend fun guestLogin(event: WelcomeEvent.SignIn) {
        runCatching { sessionRepository.enterAsGuest() }
            .onSuccess {
                viewState.update { it.copy(progress = false, entered = true) }
            }
            .onFailure { error ->
                logNonFatal(error)

                when (error) {
                    is IOException -> {
                        viewState.update {
                            it.copy(
                                progress = false,
                                entered = false,
                                enabledEnterModes = it.enabledEnterModes - event.enterMode
                            )
                        }
                    }

                    is IllegalStateException, is CancellationException -> {
                        viewState.update { it.copy(progress = false, entered = false) }
                    }

                    else -> {
                        throw error
                    }
                }
            }
    }
}
