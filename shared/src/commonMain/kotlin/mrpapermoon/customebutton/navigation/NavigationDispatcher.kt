package mrpapermoon.customebutton.navigation

import mrpapermoon.customebutton.ui.navigation.NavigationEvent

/**
 * Presentation contract for forwarding navigation events to the active host.
 * The host owns destination resolution and back-stack changes.
 */
fun interface NavigationDispatcher {
    /** Dispatches [event] to the host navigation controller. */
    fun onNavigationEvent(event: NavigationEvent)
}
