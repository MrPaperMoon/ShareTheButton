package mrpapermoon.customebutton.ui.action

import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.ui.common.ViewError

data class ActionViewState(
    val loading: Boolean = true,
    val button: SharedButton? = null,
    val showNotice: Boolean = false,
    val error: ViewError? = null
)
