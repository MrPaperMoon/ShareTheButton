package mrpapermoon.customebutton.qr

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Presentation-support contract for converting an invitation URI to a Compose bitmap.
 *
 * The bitmap encodes the raw URI used by the invitation. The host supplies a generator
 * when QR rendering is available. Android currently uses ZXing.
 */
fun interface QrGenerator {
    /**
     * Creates a QR image for the supplied [link].
     *
     * @return A bitmap ready to render in Compose.
     */
    suspend fun generate(link: String): ImageBitmap
}
