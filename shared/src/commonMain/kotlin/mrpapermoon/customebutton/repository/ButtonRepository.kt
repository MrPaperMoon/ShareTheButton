package mrpapermoon.customebutton.repository

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import mrpapermoon.customebutton.data.RemoteDataException
import mrpapermoon.customebutton.data.database.ButtonEntity
import mrpapermoon.customebutton.entity.SharedButton
import kotlin.coroutines.cancellation.CancellationException

/**
 * Application-facing repository contract for owned buttons and invitation publication.
 *
 * Defines the operations presentation uses to observe, save and share buttons. Persistence
 * and publication are delegated to the data-layer implementation.
 *
 * [buttons] reflects the local database independently of the remote backend. [add] saves a
 * new owned button on the device. [share] keeps the invitation token locally before it
 * publishes the button, so a network or storage failure can be retried with the same token.
 *
 * Receiving invitations and refreshing received buttons are reserved for a later flow.
 * The current implementation throws [NotImplementedError] from [join] and [load].
 *
 * @property buttons Local button flow.
 */
interface ButtonRepository {
    val buttons: Flow<List<SharedButton>>

    /** Returns the local row for [id], including ownership and the invitation token, or null if absent. */
    suspend fun get(id: String): ButtonEntity?

    /**
     * Saves an owned button to the local database.
     *
     * @param button Button with a caller-assigned ID. The ID must be absent from local storage.
     * @throws ButtonAlreadyExistException If the local database already contains this ID.
     * @throws SQLiteException If the local database operation fails.
     * @throws IllegalStateException If Room cannot open or use the database, for example after closure or when its
     *     configuration is inconsistent.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(
        SQLiteException::class,
        IllegalStateException::class,
        CancellationException::class,
        ButtonAlreadyExistException::class,
    )
    suspend fun add(button: ButtonEntity)

    /**
     * Reserved for refreshing received buttons.
     *
     * @throws NotImplementedError Always in the current implementation.
     */
    @Throws(NotImplementedError::class)
    suspend fun load()

    /**
     * Publishes an owned button and reuses its locally saved invitation token on retry.
     *
     * @param buttonId ID of the local button to publish.
     * @return The invitation token after the selected backend confirms the write.
     * @throws IllegalArgumentException If the button is missing or is not owned.
     * @throws SQLiteException If reading or storing the local invitation fails.
     * @throws RemoteDataException If Firebase is selected and the publication fails.
     * @throws IllegalStateException If Room cannot open or use the database, for example after closure or when its
     *     configuration is inconsistent.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(
        SQLiteException::class,
        RemoteDataException::class,
        IllegalArgumentException::class,
        IllegalStateException::class,
        CancellationException::class,
    )
    suspend fun share(buttonId: String): String

    /**
     * Reserved for importing a button through an invitation.
     *
     * @param token Token supplied by the sender.
     * @return The button added to the local list when the operation is implemented.
     * @throws NotImplementedError Always in the current implementation.
     */
    @Throws(NotImplementedError::class)
    suspend fun join(token: String): SharedButton

    /** Reports an attempt to insert a button whose ID already exists locally. */
    class ButtonAlreadyExistException(button: ButtonEntity) : Exception("Button $button already exists")
}
