package mrpapermoon.customebutton.ui.welcome

import kotlinx.coroutines.flow.StateFlow
import mrpapermoon.customebutton.ui.common.CommonViewModel

interface WelcomeViewModelContract : CommonViewModel, WelcomeEventHandler {
    val viewState: StateFlow<WelcomeViewState>
}
