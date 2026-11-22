package mrpapermoon.customebutton.data

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mrpapermoon.customebutton.data.database.ButtonDao
import mrpapermoon.customebutton.data.database.ButtonEntity
import mrpapermoon.customebutton.data.database.toEntity
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.Uuid

/**
 * Data-layer implementation of [ButtonRepository] coordinating local persistence and
 * remote publication. It enforces ownership checks and invitation-token reuse.
 *
 * Room is the source of truth for [buttons]. The repository rejects any save whose
 * ID already exists. [share]
 * serializes publication attempts, checks ownership and persists the token before calling
 * the remote source. That token remains available if publication fails.
 *
 * @param dao Local button DAO.
 * @param remote Invitation backend.
 * @property buttons Local button flow in database order.
 */
class ButtonRepositoryImpl(
    private val dao: ButtonDao,
    private val remote: ButtonRemoteDataSource,
) : ButtonRepository {
    private val sharingMutex = Mutex()

    override val buttons: Flow<List<SharedButton>>
        get() = dao.observeAll().map { rows -> rows.map { it.toButton() } }

    override suspend fun get(id: String): ButtonEntity? = dao.find(id)

    /**
     * Stores an owned button following the duplicate-ID policy of [ButtonRepository.add].
     *
     * @throws SQLiteException If SQLite cannot read or persist the button rows, including constraint violations or
     *     database I/O failures.
     * @throws IllegalStateException If Room cannot open or use the database, for example after closure or when its
     *     configuration is inconsistent.
     * @throws CancellationException If the calling coroutine is cancelled while waiting for storage, authentication or
     *     publication; a remote operation may also time out.
     * @throws ButtonRepository.ButtonAlreadyExistException If a row with this ID already exists.
     */
    @Throws(
        SQLiteException::class,
        IllegalStateException::class,
        CancellationException::class,
        ButtonRepository.ButtonAlreadyExistException::class,
    )
    override suspend fun add(button: ButtonEntity) {
        val existing = dao.find(button.id)
        if (existing != null) throw ButtonRepository.ButtonAlreadyExistException(button)

        dao.insert(button)
    }

    /**
     * Reserved for refreshing received buttons.
     *
     * @throws NotImplementedError Refresh is not implemented.
     */
    @Throws(NotImplementedError::class)
    override suspend fun load() {
        TODO()
    }

    /**
     * Persists the invitation token before publication so a failed attempt can be retried with the same token.
     *
     * @param buttonId ID of the owned button.
     * @return The persisted token after the backend confirms publication.
     * @see ButtonRepository.share
     * @throws SQLiteException If SQLite cannot read or persist the button rows, including constraint violations or
     *     database I/O failures.
     * @throws RemoteDataException If the platform authentication or document operation fails, including rejected
     *     credentials, denied access or network failure. The original SDK exception is retained as the cause.
     * @throws IllegalArgumentException If the button is missing, is not owned, or its ID or token cannot form a valid
     *     remote document path.
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
    override suspend fun share(buttonId: String): String {
        return sharingMutex.withLock {
            val button = requireNotNull(dao.find(buttonId)) { "Button not found" }
            require(button.isOwned) { "Only owned buttons can be published" }
            val token = button.shareToken ?: Uuid.random().toString()
            // Keep the token before uploading so retries reuse the same invitation.
            dao.upsert(button.copy(shareToken = token))
            remote.publish(token, button.toButton())
            token
        }
    }

    /**
     * Reserved for importing a button by invitation.
     *
     * @param token Invitation to resolve.
     * @throws NotImplementedError Import is not implemented.
     */
    @Throws(NotImplementedError::class)
    override suspend fun join(token: String): SharedButton {
        TODO()
    }
}
