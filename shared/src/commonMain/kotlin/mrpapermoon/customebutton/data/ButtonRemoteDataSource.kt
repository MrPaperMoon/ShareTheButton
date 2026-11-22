package mrpapermoon.customebutton.data

import kotlin.coroutines.cancellation.CancellationException
import mrpapermoon.customebutton.data.RemoteDataException
import mrpapermoon.customebutton.entity.SharedButton

/**
 * Publishes buttons and resolves them by invitation token.
 *
 * The token identifies an invitation independently of the button ID. [publish] completes
 * when the selected backend has written the invitation. [fetch] returns a button for a
 * known token or `null` if it is unavailable. Android release uses Firestore and Android
 * debug uses a separate Room database.
 */
interface ButtonRemoteDataSource {
    /**
     * Publishes a button for the given invitation token. Repeating a publication can update that token.
     *
     * @param token Invitation token generated separately from the button ID.
     * @param button Button to make available to recipients.
     * @throws RemoteDataException If the Firebase implementation cannot authenticate or write the records.
     * @throws IllegalArgumentException If a document path is empty or has an invalid collection/document structure, or
     *     document fields cannot be encoded by the platform SDK.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(RemoteDataException::class, IllegalArgumentException::class, CancellationException::class)
    suspend fun publish(token: String, button: SharedButton)
    /**
     * Resolves an invitation from the selected storage backend.
     *
     * @param token Invitation token used when publishing.
     * @return The published button, or `null` when the invitation or its button cannot be found.
     * @throws RemoteDataException If the Firebase implementation cannot authenticate or read the records.
     * @throws IllegalArgumentException If a document path is empty or has an invalid collection/document structure, or
     *     document fields cannot be encoded by the platform SDK.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(
        RemoteDataException::class,
        IllegalArgumentException::class,
        CancellationException::class,
    )
    suspend fun fetch(token: String): SharedButton?
}
