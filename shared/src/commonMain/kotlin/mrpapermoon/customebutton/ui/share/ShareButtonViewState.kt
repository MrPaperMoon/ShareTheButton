package mrpapermoon.customebutton.ui.share

import androidx.compose.ui.graphics.ImageBitmap
import mrpapermoon.customebutton.entity.SharedButton

/**
 * Represents one publication attempt and its result.
 *
 * The local button can be available for preview while publication is still running.
 * A link is set only after the backend confirms the write. [LinkState.Failed] exposes
 * a retry action, and the QR bitmap is added after rendering completes.
 *
 * @property button Preview button.
 * @property linkState Publication state.
 * @property qr QR image generated from sharing link.
 */
data class ShareButtonViewState(
    val button: SharedButton = SharedButton.stub(),
    val linkState: LinkState = LinkState.Loading,
    val qr: ImageBitmap? = null,
    val effect: ShareButtonEffect? = null,
) {
    sealed class LinkState {
        open val link: String? = null

        data object Loading : LinkState()
        data object Failed : LinkState()
        data class Ready(override val link: String) : LinkState()
    }
}

