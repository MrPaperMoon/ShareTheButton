package mrpapermoon.customebutton.data

import androidx.compose.ui.graphics.Color
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import mrpapermoon.customebutton.entity.SharedButton
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.UUID

class StubButtonRemoteDataSourceTest {
    @Test
    fun resolvesTokensAfterReopeningDatabase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbName = "remote-stub-test-${UUID.randomUUID()}.db"
        fun open() = Room.databaseBuilder<StubButtonDatabase>(context, dbName).build()
        val button = SharedButton("button-id", Color(0xA0123456), "Title", "Action", "Notification")

        try {
            val writer = open()
            try {
                val source = StubButtonRemoteDataSource(writer.buttons())
                assertNull(source.fetch("unknown-token"))
                source.publish("invite", button)
                source.publish("invite", button.copy(title = "Updated"))
            } finally {
                writer.close()
            }

            val reader = open()
            try {
                val source = StubButtonRemoteDataSource(reader.buttons())
                assertEquals(button.copy(title = "Updated"), source.fetch("invite"))
                assertNull(source.fetch(button.id))
            } finally {
                reader.close()
            }
        } finally {
            context.deleteDatabase(dbName)
        }
    }
}
