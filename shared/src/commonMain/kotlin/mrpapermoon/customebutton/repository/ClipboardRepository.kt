package mrpapermoon.customebutton.repository

/**
 * Platform-service contract used by presentation to copy invitation text.
 *
 * [copy] replaces the current contents with plain text and is called from the UI thread.
 * The caller owns any success message shown to the user.
 */
fun interface ClipboardRepository {
    /**
     * Replaces the clipboard contents with [text].
     *
     * Call from the UI thread.
     */
    fun copy(text: String)
}
