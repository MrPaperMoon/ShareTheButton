package mrpapermoon.customebutton.data

import kotlin.coroutines.cancellation.CancellationException

/**
 * Data-layer port supplying an authenticated owner for remote button operations.
 * Implementations reuse the current user and create an anonymous session when needed.
 */
interface AuthFacade {
    /**
     * Returns the current or newly authenticated user ID.
     *
     * @throws IllegalArgumentException If authentication completes without a user ID.
     * @throws RemoteDataException If the platform SDK rejects the operation or cannot reach its service. The original
     *     SDK exception is retained as the cause.
     * @throws CancellationException If the caller cancels while waiting for the platform SDK result.
     */
    @Throws(RemoteDataException::class, IllegalArgumentException::class, CancellationException::class)
    suspend fun userId(): String
}

/**
 * Data-layer port isolating document storage operations from platform Firestore SDKs.
 * Documents contain string and Long fields. Implementations preserve atomic writes and
 * read directly from the server so deleted invitation links are observed.
 */
interface FirestoreFacade {
    /**
     * Commits all documents, keyed by their collection/document path, in one batch.
     *
     * @throws RemoteDataException If the platform SDK rejects the operation or cannot reach its service. The original
     *     SDK exception is retained as the cause.
     * @throws IllegalArgumentException If a document path is empty or has an invalid collection/document structure, or
     *     document fields cannot be encoded by the platform SDK.
     * @throws CancellationException If the caller cancels while waiting for the platform SDK result.
     */
    @Throws(RemoteDataException::class, IllegalArgumentException::class, CancellationException::class)
    suspend fun writeBatch(documents: Map<String, Map<String, Any>>)

    /**
     * Returns server fields, or null when the document does not exist.
     *
     * @throws RemoteDataException If the platform SDK rejects the operation or cannot reach its service. The original
     *     SDK exception is retained as the cause.
     * @throws IllegalArgumentException If a document path is empty or has an invalid collection/document structure.
     * @throws CancellationException If the caller cancels while waiting for the platform SDK result.
     */
    @Throws(RemoteDataException::class, IllegalArgumentException::class, CancellationException::class)
    suspend fun readDocument(path: String): Map<String, Any>?
}

/** Reports a remote SDK failure while retaining its original cause for logging. */
class RemoteDataException(message: String, cause: Throwable) : Exception(message, cause)
