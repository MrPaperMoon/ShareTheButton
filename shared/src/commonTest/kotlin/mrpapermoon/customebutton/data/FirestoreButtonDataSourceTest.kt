package mrpapermoon.customebutton.data

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest
import mrpapermoon.customebutton.entity.SharedButton

class FirestoreButtonDataSourceTest {
    @Test
    fun publicationPreservesDocumentFormatAndArgbLong() = runTest {
        val firestore = MemoryFirestore()
        val source = FirestoreButtonDataSource(firestore, OwnerAuth)
        val button = SharedButton("button", Color(0xFF9DE0C3), "Title", "Action", "Notification")

        source.publish("token", button)

        assertEquals(1, firestore.batches)
        assertEquals(mapOf("ownerId" to "owner", "buttonId" to "button"), firestore.documents["buttonLinks/token"])
        assertEquals(
            mapOf(
                "ownerId" to "owner", "id" to "button", "color" to 0xFF9DE0C3L,
                "title" to "Title", "actionText" to "Action", "notificationText" to "Notification"
            ),
            firestore.documents["sharedButtons/button"]
        )
        assertEquals(button, source.fetch("token"))
    }

    @Test
    fun missingLinkOrIncompleteButtonReturnsNull() = runTest {
        val firestore = MemoryFirestore()
        val source = FirestoreButtonDataSource(firestore, OwnerAuth)
        assertNull(source.fetch("missing"))
        firestore.documents["buttonLinks/token"] = mapOf("buttonId" to "button")
        assertNull(source.fetch("token"))
        firestore.documents["sharedButtons/button"] = mapOf("id" to "button")
        assertNull(source.fetch("token"))
    }

    private object OwnerAuth : AuthFacade {
        override suspend fun userId(): String = "owner"
    }

    private class MemoryFirestore : FirestoreFacade {
        val documents = mutableMapOf<String, Map<String, Any>>()
        var batches = 0
        override suspend fun writeBatch(documents: Map<String, Map<String, Any>>) {
            batches++
            this.documents.putAll(documents)
        }
        override suspend fun readDocument(path: String): Map<String, Any>? = documents[path]
    }
}
