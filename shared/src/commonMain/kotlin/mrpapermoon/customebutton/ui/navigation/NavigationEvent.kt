package mrpapermoon.customebutton.ui.navigation

import mrpapermoon.customebutton.entity.Screen

/** Commands that screens send to the active navigation host. */
sealed class NavigationEvent {
    /** Opens the destination described by [screen]. */
    data class NavigateTo(val screen: Screen, val replaceCurrent: Boolean = false) : NavigationEvent()
    /** Returns to the preceding destination when one exists. */
    data object PopBackStack : NavigationEvent()
    /** Removes the graph from the back stack, including its root. */
    data object FinishGraph : NavigationEvent()
}
