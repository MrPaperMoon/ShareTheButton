package mrpapermoon.customebutton.ui.create

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.ui.common.ViewError
import kotlin.uuid.Uuid

class CreateButtonViewModel(private val repository: ButtonRepository) : CreateButtonViewModelContract {
    override lateinit var coroutineScope: CoroutineScope

    override val viewState: StateFlow<CreateButtonViewState>
        field = MutableStateFlow(CreateButtonViewState())

    private val buttonId = Uuid.random().toString()

    override fun onEvent(event: CreateButtonEvent) {
        if (event == CreateButtonEvent.Save) {
            save()
        } else {
            viewState.update { it.reduce(event) }
        }
    }

    private fun save() {
        val state = viewState.value
        if (state.step != CreateButtonStep.Confirm || !state.canSave || state.saving) return
        viewState.update { it.copy(saving = true, error = null) }
        coroutineScope.launch {
            try {
                val draft = state.draft
                repository.add(
                    SharedButton(
                        buttonId,
                        draft.color,
                        draft.title.trim(),
                        draft.actionText.trim(),
                        draft.notificationText.trim()
                    )
                )
                viewState.update { it.copy(saving = false, completed = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                viewState.update { it.copy(saving = false, error = ViewError.SaveButton) }
            }
        }
    }
}
