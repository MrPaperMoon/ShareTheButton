package mrpapermoon.customebutton.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import mrpapermoon.customebutton.entity.SharedButton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

/**
 * Remote data source translating button models into Firestore documents and back.
 *
 * Owns collection names, document fields, authentication sequencing and operation
 * timeouts. Platform SDK access is delegated to [AuthFacade] and [FirestoreFacade].
 *
 * The source reuses the current Firebase user or signs in anonymously. [publish] writes
 * the button document and the token-to-button link in one batch. [fetch] reads the link
 * and then the button from the server, so it can observe missing documents rather than
 * serving stale local data. Both operations have a 30-second timeout.
 *
 * @param firestore Document-storage port.
 * @param auth Authentication port.
 */
class FirestoreButtonDataSource(
    private val firestore: FirestoreFacade,
    private val auth: AuthFacade
) : ButtonRemoteDataSource {
    private val authMutex = Mutex()

    /**
     * Writes the button document and its link document in one Firestore batch.
     *
     * @param token Document ID in the `buttonLinks` collection.
     * @param button Button written to `sharedButtons` under its own ID.
     * @throws RemoteDataException If authentication or the batch write fails.
     * @throws kotlinx.coroutines.TimeoutCancellationException If publication exceeds 30 seconds.
     * @throws IllegalArgumentException If authentication returns no user ID, or the token or button ID produces an
     *     invalid document path.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(RemoteDataException::class, IllegalArgumentException::class, CancellationException::class)
    override suspend fun publish(
        token: String,
        button: SharedButton
    ) {
        withTimeout(30_000.milliseconds) {
            val uid = userId()
            firestore.writeBatch(
                mapOf(
                    "$DATA_SOURCE_KEY/${button.id}" to mapOf(
                        "ownerId" to uid,
                        "id" to button.id,
                        "color" to button.color.toArgb().toLong(),
                        "title" to button.title,
                        "actionText" to button.actionText,
                        "notificationText" to button.notificationText
                    ),
                    "$LINKS_KEY/$token" to mapOf("ownerId" to uid, "buttonId" to button.id)
                )
            )
        }
    }

    /**
     * Looks up an invitation and its button using server reads.
     *
     * @param token Document ID in the `buttonLinks` collection.
     * @return The published button, or `null` if its records or required fields are missing.
     * @throws RemoteDataException If authentication or a Firestore read fails.
     * @throws kotlinx.coroutines.TimeoutCancellationException If the lookup exceeds 30 seconds.
     * @throws IllegalArgumentException If authentication returns no user ID, or the token or button ID produces an
     *     invalid document path.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(
        RemoteDataException::class,
        IllegalArgumentException::class,
        CancellationException::class,
    )
    override suspend fun fetch(token: String): SharedButton? {
        return withTimeout(30_000.milliseconds) {
            userId()
            val invitation = firestore.readDocument("$LINKS_KEY/$token") ?: return@withTimeout null
            val buttonId = invitation["buttonId"] as String
            val document = firestore.readDocument("$DATA_SOURCE_KEY/$buttonId") ?: return@withTimeout null

            SharedButton(
                id = document["id"] as String,
                color = Color(document["color"] as Int),
                title = document["title"] as String,
                actionText = document["actionText"] as String,
                notificationText = document["notificationText"] as String
            )
        }
    }

    /**
     * Serializes requests for the publication owner.
     *
     * @throws IllegalArgumentException If authentication completes without a user ID.
     * @throws RemoteDataException If the platform authentication or document operation fails, including rejected
     *     credentials, denied access or network failure. The original SDK exception is retained as the cause.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     */
    @Throws(RemoteDataException::class, IllegalArgumentException::class, CancellationException::class)
    private suspend fun userId(): String {
        return authMutex.withLock {
            auth.userId()
        }
    }

    private companion object {
        const val DATA_SOURCE_KEY = "sharedButtons"
        const val LINKS_KEY = "buttonLinks"
    }
}
