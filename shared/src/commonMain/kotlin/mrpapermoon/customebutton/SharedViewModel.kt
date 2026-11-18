package mrpapermoon.customebutton

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.repository.SessionRepository

interface SharedViewModel : SharedViewModelContract {
    val sessionRepository: SessionRepository

    override val isLoggedIn: MutableStateFlow<Boolean?>

    override fun onUiReady() {
        coroutineScope.launch {
            isLoggedIn.value = sessionRepository.hasSession()
        }
    }
}
