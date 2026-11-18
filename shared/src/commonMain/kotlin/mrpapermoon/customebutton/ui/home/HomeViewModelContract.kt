package mrpapermoon.customebutton.ui.home

import kotlinx.coroutines.flow.StateFlow
import mrpapermoon.customebutton.ui.common.CommonViewModel

interface HomeViewModelContract : CommonViewModel, HomeEventHandler {
    val viewState: StateFlow<HomeViewState>
}
