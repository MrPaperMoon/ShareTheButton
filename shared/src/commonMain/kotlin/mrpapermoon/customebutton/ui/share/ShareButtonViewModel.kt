package mrpapermoon.customebutton.ui.share

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mrpapermoon.customebutton.data.RemoteDataException
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.logging.logNonFatal
import mrpapermoon.customebutton.qr.QrGenerator
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.repository.ClipboardRepository
import mrpapermoon.customebutton.ui.Event
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.stickToEvent
import mrpapermoon.customebutton.usecase.ShareInvitationUseCase

/**
 * Presentation model coordinating publication and the share, copy and QR actions
 * for a selected button. Storage and token reuse belong to [ButtonRepository].
 *
 * Loads the selected local row, publishes its invitation and builds the deeplink used
 * by the QR generator. [onEvent] handles copy, share and retry commands from the screen.
 * The published URI is exposed through [ShareButtonViewState.LinkState].
 *
 * @param repository Button storage and publication.
 * @param buttonId Local button ID.
 * @param shareInvitation System share action.
 * @param clipboard Platform clipboard.
 * @param qrGenerator QR renderer.
 * @property viewState Publication state.
 */
@AssistedInject
class ShareButtonViewModel(
    private val repository: ButtonRepository,
    @Assisted private val buttonId: String,
    private val shareInvitation: ShareInvitationUseCase,
    private val clipboard: ClipboardRepository,
    private val qrGenerator: QrGenerator,
) : ViewModel(), EventHandler {
    val viewState: StateFlow<ShareButtonViewState>
        field = MutableStateFlow(ShareButtonViewState())

    init {
        viewModelScope.launch {
            val button = repository.get(buttonId)!!.run {
                SharedButton(
                    id = id,
                    color = Color(color),
                    title = title,
                    actionText = actionText,
                    notificationText = notificationText,
                )
            }
            viewState.update { it.copy(button = button) }

            publishLink()
        }
    }

    override fun onEvent(event: Event) {
        stickToEvent<ShareButtonEvent>(event)
        super.onEvent(event)
        when (event) {
            is ShareButtonEvent.OnCopyClick -> {
                clipboard.copy(event.link)
                viewState.update { it.copy(effect = ShareButtonEffect.OnCopyLink) }
            }

            is ShareButtonEvent.OnShareClick -> {
                viewModelScope.launch { shareInvitation(event.link) }
            }

            ShareButtonEvent.Retry -> {
                viewModelScope.launch { publishLink() }
            }
        }
    }

    suspend fun publishLink() {
        val link = runCatching {
            "customebutton://join/${repository.share(buttonId)}"
        }
            .onSuccess { token ->
                viewState.update { it.copy(linkState = ShareButtonViewState.LinkState.Ready(token)) }
            }
            .onFailure { error ->
                when (error) {
                    is SQLiteException,
                    is RemoteDataException,
                    is IllegalArgumentException,
                    is IllegalStateException -> {
                        logNonFatal(error) { "Button invitation publication timed out" }
                        viewState.update { it.copy(linkState = ShareButtonViewState.LinkState.Failed) }
                    }
                }
            }
            .getOrNull()

        if (link != null) {
            val imageBitmap = qrGenerator.generate(link)
            viewState.update { it.copy(qr = imageBitmap) }
        }
    }

    /** Metro assisted factory for a model with a navigation-supplied button ID. */
    @AssistedFactory
    fun interface Factory {
        /** Creates the screen model for the given [buttonId]. */
        fun create(buttonId: String): ShareButtonViewModel
    }
}
