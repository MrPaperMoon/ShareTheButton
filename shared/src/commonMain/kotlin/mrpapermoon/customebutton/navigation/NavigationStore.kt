package mrpapermoon.customebutton.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import mrpapermoon.customebutton.ui.navigation.NavigationEvent

interface NavigationActor {
    fun onNavigationEvent(event: NavigationEvent)
}

interface NavigationReducer {
    val state: StateFlow<NavigationState>
}

class NavigationStore(private val reducer: (NavigationEvent) -> NavigationState) : NavigationActor,
    NavigationReducer {
    override val state = MutableStateFlow(NavigationState())

    override fun onNavigationEvent(event: NavigationEvent) {
        state.update {
            it reduce event
        }
    }

    private infix fun NavigationState.reduce(event: NavigationEvent): NavigationState {
        return when (event) {
            is NavigationEvent.NavigateTo -> {
                if (currentScreen == event.screen.path) return this
                reducer(event)
            }

            NavigationEvent.FinishGraph,
            NavigationEvent.PopBackStack -> reducer(event)
        }
    }

    companion object {
        val stub = NavigationStore { NavigationState() }
    }
}
