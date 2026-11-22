package mrpapermoon.customebutton.data

import androidx.compose.ui.graphics.Color
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import mrpapermoon.customebutton.data.database.ButtonDatabase
import mrpapermoon.customebutton.data.database.buildButtonDatabase
import mrpapermoon.customebutton.data.database.toEntity
import mrpapermoon.customebutton.entity.SharedButton
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ButtonRepositoryRoomTest {
    @Test
    fun persistsButtonsAfterReopening() = runTest {
        val path = Files.createTempDirectory("button-room-test").resolve("buttons.db")
        fun database() = buildButtonDatabase(Room.databaseBuilder<ButtonDatabase>(path.toString()))
        var db = database()
        try {
            var repository = ButtonRepositoryImpl(db.buttons(), FakeRemote())
            assertTrue(repository.buttons.first().isEmpty())
            repository.add(button("new").copy(notificationText = "Line\n\"Quote\" \\ в™Ў").toEntity())
            val expected = repository.buttons.first()
            assertEquals(0xFF9DE0C3.toInt(), db.buttons().find("new")?.color)
            assertEquals(Color(0xFF9DE0C3), expected.single().color)
            db.close()
            db = database()
            repository = ButtonRepositoryImpl(db.buttons(), FakeRemote())
            assertEquals(expected, repository.buttons.first())
            assertEquals(1, repository.buttons.first().size)
        } finally {
            db.close()
            path.parent.toFile().deleteRecursively()
        }
    }

    @Test
    fun concurrentAddsKeepAllRowsAndDuplicateDoesNotOverwrite() = runTest {
        val db = memoryDatabase()
        try {
            val repository = ButtonRepositoryImpl(db.buttons(), FakeRemote())
            (1..20).map { async(Dispatchers.Default) { repository.add(button(it.toString()).toEntity()) } }.awaitAll()
            assertEquals(20, repository.buttons.first().size)
            assertFails { repository.add(button("1").copy(title = "Overwrite").toEntity()) }
            assertEquals("Hello", db.buttons().find("1")?.title)
        } finally {
            db.close()
        }
    }

    @Test
    fun shareRetriesSameToken() = runTest {
        val db = memoryDatabase()
        try {
            val remote = FakeRemote()
            val owner = ButtonRepositoryImpl(db.buttons(), remote)
            owner.add(button("same-id").toEntity())
            remote.fail = true
            assertFailsWith<IllegalStateException> { owner.share("same-id") }
            val pendingToken = db.buttons().find("same-id")?.shareToken
            remote.fail = false
            val token = owner.share("same-id")
            assertEquals(pendingToken, token)
            assertEquals(token, owner.share("same-id"))
            assertEquals(button("same-id"), remote.values[token])
        } finally {
            db.close()
        }
    }

    private fun memoryDatabase(): ButtonDatabase =
        buildButtonDatabase(Room.inMemoryDatabaseBuilder<ButtonDatabase>())

    private fun button(id: String) = SharedButton(id, Color(0xFF9DE0C3), "Hello", "SOS", "Hi!")

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
}
