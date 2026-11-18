package mrpapermoon.customebutton.ui.home

import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.ui.common.ViewError

data class HomeViewState(
    val buttons: List<SharedButton> = emptyList(),
    val error: ViewError? = null
)
