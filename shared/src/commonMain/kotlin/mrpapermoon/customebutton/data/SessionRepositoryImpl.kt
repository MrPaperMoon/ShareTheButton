package mrpapermoon.customebutton.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.storage.LightDataStorage
import kotlin.uuid.Uuid

class SessionRepositoryImpl(
    private val storage: LightDataStorage,
    private val dispatcher: CoroutineDispatcher
) : SessionRepository {
    override suspend fun hasSession(): Boolean = withContext(dispatcher) {
        storage.contains(SESSION_KEY, String::class)
    }

    @Throws(RuntimeException::class)
    override suspend fun enterAsGuest(): Unit = withContext(dispatcher) {
        storage.put(SESSION_KEY, Uuid.random().toString())
    }

    companion object {
        const val SESSION_KEY = "session"
    }
}
