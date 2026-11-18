package mrpapermoon.customebutton.ui.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.repository.ButtonRepository

interface HomeViewModel : HomeViewModelContract {
    val repository: ButtonRepository

    override val viewState: MutableStateFlow<HomeViewState>

    fun postInit() {
        coroutineScope.launch {
            repository.buttons.collect { buttons ->
                viewState.update { it.copy(buttons = buttons) }
            }
        }
    }

    override fun onEvent(event: HomeEvent) {
    }
}
