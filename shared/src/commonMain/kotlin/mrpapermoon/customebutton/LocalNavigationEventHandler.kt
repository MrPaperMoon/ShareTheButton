package mrpapermoon.customebutton

import androidx.compose.runtime.staticCompositionLocalOf
import mrpapermoon.customebutton.navigation.NavigationDispatcher

/** Navigation dispatcher supplied by the active host. Reading it without a provider fails. */
val LocalNavigationEventHandler = staticCompositionLocalOf<NavigationDispatcher> {
    error("CompositionLocal LocalNavigationEventHandler not present")
}

