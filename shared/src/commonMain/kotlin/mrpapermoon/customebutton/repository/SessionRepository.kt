package mrpapermoon.customebutton.repository

import androidx.datastore.core.IOException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Application-facing repository contract for remembering guest entry on this installation.
 *
 * [enterAsGuest] writes a local session identifier. [hasSession] checks its presence so
 * the app can restore the guest entry decision on the next launch. Firebase authentication
 * for publication has its own lifecycle.
 */
interface SessionRepository {
    /**
     * Checks whether a local guest session is present.
     *
     * @return `true` when a guest previously entered on this installation.
     * @throws IOException If session storage cannot be read.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(IOException::class, IllegalStateException::class, CancellationException::class)
    suspend fun hasSession(): Boolean

    /**
     * Writes a local guest session, replacing any previous session value.
     *
     * @throws IOException If the session cannot be written.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(IOException::class, IllegalStateException::class, CancellationException::class)
    suspend fun enterAsGuest()
}
