package mrpapermoon.customebutton.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import mrpapermoon.customebutton.repository.SessionRepository
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModelImpl @Inject constructor(
    override val sessionRepository: SessionRepository
) : ViewModel(), WelcomeViewModel {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val viewState = MutableStateFlow(WelcomeViewState())
}
