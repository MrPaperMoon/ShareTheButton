package mrpapermoon.customebutton.storage

import kotlin.coroutines.cancellation.CancellationException
import androidx.datastore.core.IOException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.first
import kotlin.reflect.KClass

/**
 * Local data-layer adapter providing typed key-value storage over Preferences DataStore.
 *
 * Repositories choose keys and interpret values. This adapter handles supported types,
 * snapshot reads and persistent writes.
 *
 * [put] waits for the DataStore transaction to finish. [getOrNull] reads a snapshot
 * and distinguishes a missing preference from a stored value of the wrong type.
 * [contains] checks the key name without validating the stored value. Supported types
 * are [String], [Int], [Long], [Float], [Boolean] and [ByteArray].
 *
 * @param dataStore Backing DataStore.
 */
@Inject
@SingleIn(AppScope::class)
class LightDataStorage(private val dataStore: DataStore<Preferences>) {
    /**
     * Writes a preference and waits for the transaction to complete.
     *
     * @param key Preference name.
     * @param value Value of a supported type.
     * @throws IllegalArgumentException If the value type is unsupported.
     * @throws IOException If the transaction cannot be saved.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(
        IOException::class,
        IllegalStateException::class,
        CancellationException::class,
        IllegalArgumentException::class,
    )
    suspend fun <T : Any> put(key: String, value: T) {
        @Suppress("UNCHECKED_CAST")
        val preferenceKey = preferenceKey(key, value::class) as Preferences.Key<T>
        dataStore.edit { it[preferenceKey] = value }
    }

    /**
     * Reads a preference from the current persisted snapshot.
     *
     * @param key Preference name.
     * @param clazz Expected runtime type.
     * @return The stored value, or `null` if the key is absent.
     * @throws IllegalArgumentException If the requested type is unsupported.
     * @throws ClassCastException If the stored value has a different type.
     * @throws IOException If the snapshot cannot be read.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(
        IOException::class,
        IllegalStateException::class,
        CancellationException::class,
        IllegalArgumentException::class,
        ClassCastException::class,
    )
    suspend fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T? {
        val value = dataStore.data.first()[preferenceKey(key, clazz)] ?: return null
        if (!clazz.isInstance(value)) throw ClassCastException("Preference '$key' is not a ${clazz.simpleName}")
        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    /**
     * Checks whether the preference name is present.
     *
     * @param key Preference name.
     * @param clazz Supported type used to construct the key. The stored value type is not checked.
     * @return `true` if the preference is present.
     * @throws IllegalArgumentException If the requested type is unsupported.
     * @throws IOException If the snapshot cannot be read.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(
        IOException::class,
        IllegalStateException::class,
        CancellationException::class,
        IllegalArgumentException::class,
    )
    suspend fun <T : Any> contains(key: String, clazz: KClass<T>): Boolean =
        dataStore.data.first().contains(preferenceKey(key, clazz))

    /**
     * Reads the Boolean preference named [key], defaulting to `false` if absent.
     *
     * @throws ClassCastException If the value is not a Boolean.
     * @throws IOException If the snapshot cannot be read.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(
        IOException::class,
        IllegalStateException::class,
        CancellationException::class,
        ClassCastException::class,
    )
    suspend fun getBoolean(key: String): Boolean = getOrNull(key, Boolean::class) ?: false
    /**
     * Reads the required String preference named [key].
     *
     * @throws NullPointerException If the key is absent.
     * @throws ClassCastException If the value is not a String.
     * @throws IOException If the snapshot cannot be read.
     * @throws IllegalStateException If DataStore detects multiple active instances using the same file or rejects an
     *     invalid storage state.
     * @throws CancellationException If the caller cancels while waiting for a DataStore snapshot or transaction.
     */
    @Throws(
        IOException::class,
        IllegalStateException::class,
        CancellationException::class,
        ClassCastException::class,
        NullPointerException::class,
    )
    suspend fun getString(key: String): String = getOrNull(key, String::class)!!

    /**
     * Creates a preferences key for a supported runtime type.
     *
     * @throws IllegalArgumentException If the requested or supplied value type is outside String, Int, Long, Float,
     *     Boolean and ByteArray.
     */
    @Throws(IllegalArgumentException::class)
    private fun preferenceKey(key: String, clazz: KClass<*>): Preferences.Key<*> =
        when (clazz) {
            String::class -> stringPreferencesKey(key)
            Int::class -> intPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            ByteArray::class -> byteArrayPreferencesKey(key)
            else -> throw IllegalArgumentException("Unsupported storage type: $clazz")
        }
}
