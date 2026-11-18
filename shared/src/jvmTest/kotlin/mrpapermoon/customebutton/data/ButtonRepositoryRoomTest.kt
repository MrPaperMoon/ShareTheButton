package mrpapermoon.customebutton.data

import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import mrpapermoon.customebutton.data.database.ButtonDatabase
import mrpapermoon.customebutton.data.database.buildButtonDatabase
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.storage.LightDataStorage
import java.nio.file.Files
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ButtonRepositoryRoomTest {
    @Test
    fun migratesLegacyOnceAndPersistsAfterReopening() = runTest {
        val path = Files.createTempDirectory("button-room-test").resolve("buttons.db")
        val storage = MemoryStorage()
        storage.put(
            "buttons",
            """[{"id":"old","color":"Pink","title":"Miss you","action":"SOS","notification":"Hello"}]"""
        )
        fun database() = buildButtonDatabase(Room.databaseBuilder<ButtonDatabase>(path.toString()))
        var db = database()
        try {
            var repository = ButtonRepositoryImpl(db.buttons(), FakeRemote(), storage)
            assertEquals(listOf("old"), repository.buttons.first().map { it.id })
            repository.add(button("new").copy(notificationText = "Line\n\"Quote\" \\ ♡"))
            val expected = repository.buttons.first()
            db.close()
            db = database()
            repository = ButtonRepositoryImpl(db.buttons(), FakeRemote(), storage)
            assertEquals(expected, repository.buttons.first())
            assertEquals(2, repository.buttons.first().size)
        } finally {
            db.close()
            path.parent.toFile().deleteRecursively()
        }
    }

    @Test
    fun concurrentAddsKeepAllRowsAndDuplicateDoesNotOverwrite() = runTest {
        val db = memoryDatabase()
        try {
            val repository = ButtonRepositoryImpl(db.buttons(), FakeRemote(), MemoryStorage())
            (1..20).map { async(Dispatchers.Default) { repository.add(button(it.toString())) } }.awaitAll()
            assertEquals(20, repository.buttons.first().size)
            assertFails { repository.add(button("1").copy(title = "Overwrite")) }
            assertEquals("Hello", db.buttons().find("1")?.title)
        } finally {
            db.close()
        }
    }

    @Test
    fun shareRetriesSameTokenAndJoinDoesNotOverwriteOwnedId() = runTest {
        val db = memoryDatabase()
        val otherDb = memoryDatabase()
        try {
            val remote = FakeRemote()
            val owner = ButtonRepositoryImpl(db.buttons(), remote, MemoryStorage())
            val recipient = ButtonRepositoryImpl(otherDb.buttons(), remote, MemoryStorage())
            owner.add(button("same-id"))
            recipient.add(button("same-id").copy(title = "Mine"))
            remote.fail = true
            assertFailsWith<IllegalStateException> { owner.share("same-id") }
            val pendingToken = db.buttons().find("same-id")?.shareToken
            remote.fail = false
            val token = owner.share("same-id")
            assertEquals(pendingToken, token)
            assertEquals(token, owner.share("same-id"))
            val joined = recipient.join(token)
            assertEquals(joined, recipient.join(token))
            assertEquals(2, recipient.buttons.first().size)
            assertEquals("Mine", otherDb.buttons().find("same-id")?.title)
            assertFalse(requireNotNull(otherDb.buttons().find(joined.id)).isOwned)
            assertFailsWith<IllegalArgumentException> { recipient.share(joined.id) }
            remote.values[token] = button("same-id").copy(title = "Updated")
            recipient.load()
            assertEquals("Updated", otherDb.buttons().find(joined.id)?.title)
            remote.fail = true
            assertFailsWith<IllegalStateException> { recipient.load() }
            assertEquals("Updated", otherDb.buttons().find(joined.id)?.title)
        } finally {
            db.close()
            otherDb.close()
        }
    }

    @Test
    fun malformedLegacyJsonIsNotSilentlyDiscarded() = runTest {
        val db = memoryDatabase()
        try {
            val storage = MemoryStorage()
            storage.put("buttons", "broken")
            val repository = ButtonRepositoryImpl(db.buttons(), FakeRemote(), storage)
            assertFails { repository.buttons.first() }
            assertTrue(db.buttons().getAll().isEmpty())
            assertEquals("broken", storage.getOrNull("buttons", String::class))
        } finally {
            db.close()
        }
    }

    private fun memoryDatabase(): ButtonDatabase =
        buildButtonDatabase(Room.inMemoryDatabaseBuilder<ButtonDatabase>())

    private fun button(id: String) = SharedButton(id, ButtonColor.Mint, "Hello", "SOS", "Hi!")

    private class FakeRemote : ButtonRemoteDataSource {
        val values = mutableMapOf<String, SharedButton>()
        var fail = false

        override suspend fun publish(token: String, button: SharedButton) {
            check(!fail)
            values[token] = button
        }

        override suspend fun fetch(token: String): SharedButton {
            check(!fail)
            return requireNotNull(values[token])
        }
    }

    private class MemoryStorage : LightDataStorage {
        private val values = mutableMapOf<String, Any>()

        override suspend fun <T : Any> put(key: String, value: T) {
            values[key] = value
        }

        @Suppress("UNCHECKED_CAST")
        override suspend fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T? = values[key] as T?

        override suspend fun <T : Any> contains(key: String, clazz: KClass<T>): Boolean = key in values
    }
}
