package mrpapermoon.customebutton.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Presentation reducer mapping an event and a state snapshot to the next snapshot.
 */
fun interface EventReducer<E : Event, ViewState : Any> {
    fun ViewState.reduce(event: E): ViewState
}

inline fun <reified E : Event, ViewState : Any> reduceEvent(
    event: Event,
    viewState: MutableStateFlow<ViewState>,
    reducer: ViewState.(E) -> ViewState
) {
    if (event is E) viewState.update { it.reducer(event) }
}

inline fun <reified E : Event, ViewState : Any> EventReducer<E, ViewState>.reduceEvent(
    event: Event,
    viewState: MutableStateFlow<ViewState>
) {
    if (event is E) viewState.update { it.reduce(event) }
}
