package mrpapermoon.customebutton.data

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import mrpapermoon.customebutton.storage.LightDataStorage
import kotlin.reflect.KClass
import kotlin.test.*

class RepositoriesTest {
    @Test
    fun sessionDefaultsToGuestEntryRequiredAndPersistsEntry() = runTest {
        val storage = MemoryStorage()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = SessionRepositoryImpl(storage, dispatcher)
        assertFalse(repository.hasSession())
        storage.failWrites = true
        assertFailsWith<IllegalStateException> { repository.enterAsGuest() }
        assertFalse(repository.hasSession())
        storage.failWrites = false
        repository.enterAsGuest()
        assertTrue(SessionRepositoryImpl(storage, dispatcher).hasSession())
        assertFalse(storage.getOrNull(SessionRepositoryImpl.SESSION_KEY, String::class).isNullOrBlank())
    }

    private class MemoryStorage : LightDataStorage {
        private val values = mutableMapOf<String, Any>()
        var failWrites = false

        @Suppress("UNCHECKED_CAST")
        override suspend fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T? {
            val value = values[key] ?: return null
            check(clazz.isInstance(value))
            return value as T
        }

        override suspend fun <T : Any> contains(key: String, clazz: KClass<T>): Boolean = getOrNull(key, clazz) != null

        override suspend fun <T : Any> put(key: String, value: T) {
            check(!failWrites)
            values[key] = value
        }
    }
}
