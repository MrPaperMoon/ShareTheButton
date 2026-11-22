package mrpapermoon.customebutton.ui.create

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.data.database.ButtonEntity
import mrpapermoon.customebutton.logging.logNonFatal
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.ui.Event
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.create.draft.DraftButtonEvent
import mrpapermoon.customebutton.ui.create.draft.DraftButtonEventReducer
import mrpapermoon.customebutton.ui.reduceEvent
import kotlin.concurrent.Volatile
import kotlin.uuid.Uuid

/**
 * Presentation model coordinating creation-wizard input, validation state and saving
 * through [ButtonRepository].
 *
 * [onEvent] routes wizard commands and draft edits to their respective reducers.
 * Saving validates [draftState], assigns a UUID and writes the entered text to the
 * repository. [viewState] exposes the saved ID for navigation to the sharing screen.
 * The screen chooses when to show the save action.
 *
 * @param repository Local button storage.
 * @property viewState Current wizard state.
 * @property draftState Unsaved inputs.
 */
@Inject
class CreateButtonViewModel(
    private val repository: ButtonRepository
) : ViewModel(), EventHandler {
    val viewState: StateFlow<CreateButtonViewState>
        field = MutableStateFlow(CreateButtonViewState())
    val draftState: StateFlow<ButtonUiState>
        field = MutableStateFlow(ButtonUiState())

    private val createButtonEventReducer = CreateButtonEventReducer()
    private val draftButtonEventReducer = DraftButtonEventReducer()

    @Volatile
    private var isCompleted: Boolean = false

    @Volatile
    private var isSaving: Boolean = false

    /** Applies edits and navigation events to the draft or starts a save for [event]. */
    override fun onEvent(event: Event) {
        if (isCompleted) {
            logNonFatal {
                "Incoming event after state is completed: $event"
            }
            return
        }
        if (isSaving) return
        super.onEvent(event)

        when (event) {
            CreateButtonEvent.Save -> {
                save()
            }

            is CreateButtonEvent -> {
                createButtonEventReducer.reduceEvent(event, viewState)
            }

            is DraftButtonEvent -> {
                draftButtonEventReducer.reduceEvent(event, draftState)
                if (viewState.value.error is CreateButtonEffect.InvalidInputData && draftState.value.dataIsValid) {
                    viewState.update { it.copy(error = null) }
                }
            }
        }
    }

    private fun save() {
        if (!draftState.value.dataIsValid) {
            viewState.update { it.copy(error = CreateButtonEffect.InvalidInputData) }
            return
        }

        viewState.update {
            isSaving = true
            it.copy(enabled = false, error = null)
        }

        val buttonId = Uuid.random().toString()
        viewModelScope.launch {
            runCatching {
                val draft = draftState.value
                repository.add(
                    ButtonEntity(
                        buttonId,
                        draft.color.toArgb(),
                        draft.title,
                        draft.actionText,
                        draft.notificationText
                    )
                )
            }
                .onSuccess {
                    viewState.update {
                        isCompleted = true
                        it.copy(createdButtonId = buttonId)
                    }
                }
                .onFailure { error ->
                    when (error) {
                        is ButtonRepository.ButtonAlreadyExistException -> {
                            viewState.update {
                                it.copy(
                                    enabled = true,
                                    error = CreateButtonEffect.ButtonAlreadyExist
                                )
                            }
                        }

                        is SQLiteException,
                        is IllegalStateException -> {
                            viewState.update {
                                it.copy(
                                    enabled = true,
                                    error = CreateButtonEffect.SQLiteException
                                )
                            }
                        }

                        else -> throw error
                    }
                }
        }
    }
}
