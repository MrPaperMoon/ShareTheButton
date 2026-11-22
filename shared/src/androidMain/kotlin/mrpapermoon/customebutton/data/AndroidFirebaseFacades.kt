package mrpapermoon.customebutton.data

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

/**
 * Android infrastructure adapter implementing [AuthFacade] through the official Auth SDK.
 * SDK failures retain their causes in [RemoteDataException].
 */
class AndroidAuthFacade(private val auth: FirebaseAuth) : AuthFacade {
    /**
     * Reuses the active user or awaits anonymous sign-in.
     *
     * @throws RemoteDataException If Auth reports a FirebaseException, including disabled anonymous sign-in or a
     *     network failure.
     * @throws IllegalArgumentException If sign-in completes without a user ID.
     * @throws kotlin.coroutines.cancellation.CancellationException If the caller cancels while sign-in is pending.
     */
    override suspend fun userId(): String = try {
        requireNotNull(auth.currentUser?.uid ?: auth.signInAnonymously().await().user?.uid)
    } catch (error: FirebaseException) {
        throw RemoteDataException("Firebase authentication failed", error)
    }
}

/**
 * Android infrastructure adapter implementing [FirestoreFacade] through the official Firestore SDK.
 * Batch completion waits for the server acknowledgement. Reads explicitly bypass
 * the local cache, and integer fields retain Firestore's Long representation.
 */
class AndroidFirestoreFacade(private val firestore: FirebaseFirestore) : FirestoreFacade {
    /**
     * Publishes all supplied documents in one acknowledged batch.
     *
     * @throws RemoteDataException If Firestore rejects the commit, for example because security rules deny a write.
     * @throws IllegalArgumentException If a document path is invalid or a field value cannot be encoded.
     * @throws kotlin.coroutines.cancellation.CancellationException If the caller cancels while awaiting
     *     acknowledgement. The SDK may still commit the write.
     */
    override suspend fun writeBatch(documents: Map<String, Map<String, Any>>) {
        try {
            val batch = firestore.batch()
            documents.forEach { (path, fields) -> batch.set(firestore.document(path), fields) }
            batch.commit().await()
        } catch (error: FirebaseException) {
            throw RemoteDataException("Firestore batch write failed", error)
        }
    }

    /**
     * Reads the document from the server, returning null for a missing document.
     *
     * @throws RemoteDataException If server access fails, including offline reads or permission denial.
     * @throws IllegalArgumentException If the document path is empty or has an invalid segment count.
     * @throws kotlin.coroutines.cancellation.CancellationException If the caller cancels while awaiting the read.
     */
    override suspend fun readDocument(path: String): Map<String, Any>? = try {
        firestore.document(path).get(Source.SERVER).await().data
    } catch (error: FirebaseException) {
        throw RemoteDataException("Firestore document read failed", error)
    }
}

/**
 * Connects the shared publication logic to the default Android Firebase app.
 *
 * @throws IllegalStateException If the default Firebase app has not been initialized before this factory is called.
 */
@Throws(IllegalStateException::class)
fun createFirestoreButtonDataSource(): ButtonRemoteDataSource = FirestoreButtonDataSource(
    AndroidFirestoreFacade(FirebaseFirestore.getInstance()),
    AndroidAuthFacade(FirebaseAuth.getInstance())
)
