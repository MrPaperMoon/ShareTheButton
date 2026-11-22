package mrpapermoon.customebutton.usecase

import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.share_invitation_message
import org.jetbrains.compose.resources.getString

/** Sends a localized invitation through the Android Sharesheet. */
class AndroidShareInvitationUseCase(context: Context) : ShareInvitationUseCase {
    private val context = context.applicationContext

    override suspend fun invoke(link: String) = withContext(Dispatchers.Main.immediate) {
        val message = getString(Res.string.share_invitation_message, link)
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, message)
        val chooser = Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
