package mrpapermoon.customebutton.repository

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/** Writes invitation links to the Android system clipboard. */
class AndroidClipboardRepository(context: Context) : ClipboardRepository {
    private val label = context.applicationInfo.loadLabel(context.packageManager)
    private val clipboard = context.applicationContext.getSystemService(ClipboardManager::class.java)

    override fun copy(text: String) {
        val clipData = ClipData.newPlainText(label, text)
        @Suppress("UsePropertyAccessSyntax")
        clipboard.setPrimaryClip(clipData)
    }
}
