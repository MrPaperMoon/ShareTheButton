package mrpapermoon.customebutton.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mrpapermoon.customebutton.data.database.ButtonDao
import mrpapermoon.customebutton.data.database.toEntity
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import kotlin.uuid.Uuid

class ButtonRepositoryImpl(
    private val dao: ButtonDao,
    private val remote: ButtonRemoteDataSource,
) : ButtonRepository {
    private val sharingMutex = Mutex()

    override val buttons: Flow<List<SharedButton>>
        get() = dao.observeAll().map { rows -> rows.map { it.toButton() } }

    override suspend fun add(button: SharedButton) {
        val existing = dao.find(button.id)
        if (existing != null) {
            require(existing.isOwned && existing.toButton() == button) { "Button already exists" }
            return
        }
        dao.insert(button.toEntity())
    }

    override suspend fun load() {
        TODO()
//        dao.getAll().filter { !it.isOwned }.forEach { cached ->
//            val token = requireNotNull(cached.shareToken)
//            val updated = remote.fetch(token).copy(id = cached.id)
//            dao.upsert(
//                updated.toEntity(
//                    isOwned = false,
//                    shareToken = token
//                )
//            )
//        }
    }

    override suspend fun share(buttonId: String): String {
        TODO()
//        return sharingMutex.withLock {
//            val button = requireNotNull(dao.find(buttonId)) { "Button not found" }
//            require(button.isOwned) { "Only owned buttons can be published" }
//            val token = button.shareToken ?: Uuid.random().toString()
//            // Persist before uploading so a retry reuses the same document after a network failure.
//            dao.upsert(button.copy(shareToken = token))
//            remote.publish(
//                token,
//                button.toButton()
//            )
//            token
//        }
    }

    override suspend fun join(token: String): SharedButton {
        TODO()
//        return sharingMutex.withLock {
//            val normalized = Uuid.parse(token.trim()).toString()
//            val existing = dao.getAll().find { it.shareToken == normalized }
//            if (existing?.isOwned == true) return@withLock existing.toButton()
//            // Remote IDs never overwrite local IDs, even if a remote document is malformed.
//            val button = remote.fetch(normalized).copy(id = "shared:$normalized")
//            dao.upsert(
//                button.toEntity(
//                    isOwned = false,
//                    shareToken = normalized
//                )
//            )
//            button
//        }
    }
}
