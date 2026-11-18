package mrpapermoon.customebutton.ui.action

import kotlinx.coroutines.flow.StateFlow
import mrpapermoon.customebutton.ui.common.CommonViewModel

interface ActionViewModelContract : CommonViewModel, ActionEventHandler {
    val viewState: StateFlow<ActionViewState>
}
