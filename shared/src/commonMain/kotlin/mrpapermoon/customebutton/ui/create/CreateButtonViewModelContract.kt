package mrpapermoon.customebutton.ui.create

import kotlinx.coroutines.flow.StateFlow
import mrpapermoon.customebutton.ui.common.CommonViewModel

interface CreateButtonViewModelContract : CommonViewModel, CreateButtonEventHandler {
    val viewState: StateFlow<CreateButtonViewState>
}
