package mrpapermoon.customebutton.usecase

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.share_invitation_message
import org.jetbrains.compose.resources.getString
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIViewController

/**
 * Sends a localized invitation through the iOS share sheet.
 *
 * @param presenter Returns the visible view controller for the current screen.
 */
class IosShareInvitationUseCase(private val presenter: () -> UIViewController) : ShareInvitationUseCase {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun invoke(link: String) = withContext(Dispatchers.Main.immediate) {
        val message = getString(Res.string.share_invitation_message, link)
        val controller = presenter()
        val sheet = UIActivityViewController(activityItems = listOf(message), applicationActivities = null)
        sheet.popoverPresentationController?.let { popover ->
            val view = checkNotNull(controller.view)
            popover.sourceView = view
            popover.sourceRect = view.bounds
        }
        controller.presentViewController(sheet, animated = true, completion = null)
    }
}
