package mrpapermoon.customebutton

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.repository.SessionRepository

/**
 * Application-level presentation model resolving the initial guest-entry destination.
 *
 * The model reads the local session once from its ViewModel scope. [isLoggedIn] is
 * `null` while that read is pending. The Android Activity uses the result to dismiss
 * the splash screen and choose the welcome screen or the local button list. A read
 * failure propagates to the ViewModel coroutine and leaves the state unresolved.
 *
 * @param sessionRepository Local session source.
 * @property isLoggedIn Startup session state.
 */
@Inject
class SharedViewModel(sessionRepository: SessionRepository) : ViewModel() {
    private val loggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = loggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            loggedIn.value = sessionRepository.hasSession()
        }
    }
}
