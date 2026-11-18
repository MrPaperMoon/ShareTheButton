package mrpapermoon.customebutton.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds

@Singleton
class FirestoreButtonDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ButtonRemoteDataSource {
    private val authMutex = Mutex()

    override suspend fun publish(
        token: String,
        button: SharedButton
    ) {
        withTimeout(30_000.milliseconds) {
            val uid = userId() ?: IllegalStateException("ownerId cant be null")
            firestore
                .collection(DATA_SOURCE_KEY)
                .document(token)
                .set(
                    mapOf(
                        "ownerId" to uid,
                        "id" to button.id,
                        "color" to button.color.name,
                        "title" to button.title,
                        "actionText" to button.actionText,
                        "notificationText" to button.notificationText
                    )
                ).await()
        }
    }

    override suspend fun fetch(token: String): SharedButton? {
        return withTimeout(30_000.milliseconds) {
            val document = firestore
                .collection(DATA_SOURCE_KEY)
                .document(token)
                .get(Source.SERVER)
                .await()
                .takeIf { it.exists() }

            document?.run {
                SharedButton(
                    id = getString("id") ?: return@run null,
                    color = ButtonColor.valueOf(getString("color") ?: return@run null),
                    title = getString("title") ?: return@run null,
                    actionText = getString("actionText") ?: return@run null,
                    notificationText = getString("notificationText") ?: return@run null
                )
            }
        }
    }

    private suspend fun userId(): String? {
        return authMutex.withLock {
            auth.currentUser?.uid ?: auth.signInAnonymously().await().user?.uid
        }
    }

    private companion object {
        const val DATA_SOURCE_KEY = "sharedButtons"
    }
}
