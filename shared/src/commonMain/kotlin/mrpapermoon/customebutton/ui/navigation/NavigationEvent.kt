package mrpapermoon.customebutton.ui.navigation

import mrpapermoon.customebutton.entity.Screen

sealed class NavigationEvent {
    data class NavigateTo(val screen: Screen) : NavigationEvent()
    data object PopBackStack : NavigationEvent()
    data object FinishGraph : NavigationEvent()
}
