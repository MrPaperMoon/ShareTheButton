package mrpapermoon.customebutton.data

import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.PreferencesSerializer
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import mrpapermoon.customebutton.storage.LightDataStorage
import okio.FileSystem
import okio.Path.Companion.toPath
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class LightDataStorageTest {
    @get:Rule
    val folder = TemporaryFolder()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private fun storage() = LightDataStorage(
        PreferenceDataStoreFactory.create(
            scope = scope,
            storage = OkioStorage(FileSystem.SYSTEM, PreferencesSerializer) {
                File(folder.root, "test.preferences_pb").absolutePath.toPath()
            }
        )
    )

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun roundTripsAllSupportedTypesAndReturnsNullForMissingKeys() = runBlocking {
        val storage = storage()
        assertNull(storage.getOrNull("absent", String::class))
        assertNull(storage.getOrNull("absent", Boolean::class))
        storage.put("string", "Hello\n\"world\"")
        storage.put("int", 42)
        storage.put("long", Long.MAX_VALUE)
        storage.put("float", 1.25f)
        storage.put("boolean", false)
        storage.put("bytes", byteArrayOf(1, 2))
        assertEquals("Hello\n\"world\"", storage.getOrNull("string", String::class))
        assertEquals(42, storage.getOrNull("int", Int::class))
        assertEquals(Long.MAX_VALUE, storage.getOrNull("long", Long::class))
        assertEquals(1.25f, storage.getOrNull("float", Float::class))
        assertEquals(false, storage.getOrNull("boolean", Boolean::class))
        assertArrayEquals(byteArrayOf(1, 2), storage.getOrNull("bytes", ByteArray::class))
    }

    @Test
    fun rejectsUnsupportedTypesAndMismatchedReads() = runBlocking {
        val storage = storage()
        try {
            storage.put("bad", listOf("unsupported"))
            fail("Unsupported write should fail")
        } catch (_: IllegalArgumentException) {
        }
        try {
            storage.getOrNull("bad", Double::class)
            fail("Unsupported read should fail")
        } catch (_: IllegalArgumentException) {
        }
        storage.put("value", "text")
        try {
            storage.getOrNull("value", Int::class)
            fail("Wrong type should fail")
        } catch (_: ClassCastException) {
        }
        assertEquals("text", storage.getOrNull("value", String::class))
    }
}
