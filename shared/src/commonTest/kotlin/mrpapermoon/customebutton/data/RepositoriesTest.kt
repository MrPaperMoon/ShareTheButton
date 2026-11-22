package mrpapermoon.customebutton.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import mrpapermoon.customebutton.storage.LightDataStorage

class RepositoriesTest {
    @Test
    fun sessionDefaultsToGuestEntryRequiredAndPersistsEntry() = runTest {
        val dataStore = MemoryPreferences()
        val storage = LightDataStorage(dataStore)
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = SessionRepositoryImpl(storage, dispatcher)
        assertFalse(repository.hasSession())
        dataStore.failWrites = true
        assertFailsWith<IllegalStateException> { repository.enterAsGuest() }
        assertFalse(repository.hasSession())
        dataStore.failWrites = false
        repository.enterAsGuest()
        assertTrue(SessionRepositoryImpl(storage, dispatcher).hasSession())
        assertFalse(storage.getOrNull(SessionRepositoryImpl.SESSION_KEY, String::class).isNullOrBlank())
    }

    private class MemoryPreferences : DataStore<Preferences> {
        override val data = MutableStateFlow(emptyPreferences())
        var failWrites = false

        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences {
            check(!failWrites)
            return transform(data.value).also { data.value = it }
        }
    }
}
