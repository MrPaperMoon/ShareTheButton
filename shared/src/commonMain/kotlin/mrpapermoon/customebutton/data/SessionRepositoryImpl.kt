package mrpapermoon.customebutton.data

import kotlin.coroutines.cancellation.CancellationException
import androidx.datastore.core.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.storage.LightDataStorage
import kotlin.uuid.Uuid

/**
 * Data-layer implementation of [SessionRepository] persisting guest entry in preferences.
 *
 * [enterAsGuest] generates a new UUID and writes it under [SESSION_KEY]. [hasSession]
 * checks for that key. Both operations run on the supplied dispatcher and propagate
 * storage failures to the caller.
 *
 * @param storage Local preferences.
 * @param dispatcher Dispatcher for session access.
 */
class SessionRepositoryImpl(
    private val storage: LightDataStorage,
    private val dispatcher: CoroutineDispatcher
) : SessionRepository {
    /**
     * Checks for the persisted session key. See [SessionRepository.hasSession].
     *
     * @throws IOException If DataStore cannot read or persist the preferences file.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(IOException::class, IllegalStateException::class, CancellationException::class)
    override suspend fun hasSession(): Boolean = withContext(dispatcher) {
        storage.contains(SESSION_KEY, String::class)
    }

    /**
     * Writes a newly generated UUID as the local session identifier. See [SessionRepository.enterAsGuest].
     *
     * @throws IOException If DataStore cannot read or persist the preferences file.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(IOException::class, IllegalStateException::class, CancellationException::class)
    override suspend fun enterAsGuest(): Unit = withContext(dispatcher) {
        storage.put(SESSION_KEY, Uuid.random().toString())
    }

    companion object {
        /** Preferences key for the local guest session identifier. */
        const val SESSION_KEY = "session"
    }
}
