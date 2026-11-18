package mrpapermoon.customebutton.navigation

import mrpapermoon.customebutton.entity.Screen

data class NavigationState(
    val currentScreen: String = Screen.Home.path,
)
