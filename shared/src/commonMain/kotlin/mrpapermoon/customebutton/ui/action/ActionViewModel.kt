package mrpapermoon.customebutton.ui.action

import kotlinx.coroutines.flow.StateFlow
import mrpapermoon.customebutton.repository.ButtonRepository

class ActionViewModel(
    private val repository: ButtonRepository,
) : ActionViewModelContract {

    override val viewState: StateFlow<ActionViewState>
        field = TODO()

    override fun onEvent(event: ActionEvent) {
        when (event) {
            ActionEvent.Retry -> if (!viewState.value.loading) TODO()
            ActionEvent.Press -> if (viewState.value.button != null && !viewState.value.loading && viewState.value.error == null) showNotice(
                true
            )

            ActionEvent.DismissNotice -> showNotice(false)
        }
    }

    private fun showNotice(show: Boolean) {
        TODO()
    }
}
