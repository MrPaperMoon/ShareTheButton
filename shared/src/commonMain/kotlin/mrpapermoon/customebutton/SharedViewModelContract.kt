package mrpapermoon.customebutton

import kotlinx.coroutines.flow.StateFlow
import mrpapermoon.customebutton.navigation.NavigationActor
import mrpapermoon.customebutton.navigation.NavigationReducer
import mrpapermoon.customebutton.ui.common.CommonViewModel

interface SharedViewModelContract : CommonViewModel {
    val isLoggedIn: StateFlow<Boolean?>

    fun onUiReady()
}
