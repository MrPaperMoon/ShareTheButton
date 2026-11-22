package mrpapermoon.customebutton.ui

import mrpapermoon.customebutton.logging.logBreadcrumb
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/** Receives a user interaction from the UI. */
interface EventHandler {

    /**
     * Handles a user interaction.
     *
     * @param event Interaction to dispatch.
     */
    fun onEvent(event: Event) {
        logBreadcrumb(event::toString)
    }

    companion object : EventHandler
}

@OptIn(ExperimentalContracts::class)
inline fun <reified E : Event> EventHandler.stickToEvent(event: Event) {
    contract {
        returns() implies (event is E)
    }

    if (event !is E) throw IllegalArgumentException("${this::class.simpleName} supports only ${E::class} type, but $event was received")
}
