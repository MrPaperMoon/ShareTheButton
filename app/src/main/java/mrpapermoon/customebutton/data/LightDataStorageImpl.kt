package mrpapermoon.customebutton.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import mrpapermoon.customebutton.storage.LightDataStorage
import javax.inject.Inject
import kotlin.jvm.Throws
import kotlin.reflect.KClass

internal class LightDataStorageImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LightDataStorage {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    override suspend fun <T : Any> put(key: String, value: T) {
        dataStore.edit { preferences ->
            val preferencesKey = when (value) {
                is String -> stringPreferencesKey(key)
                is Int -> intPreferencesKey(key)
                is Long -> longPreferencesKey(key)
                is Float -> floatPreferencesKey(key)
                is Boolean -> booleanPreferencesKey(key)
                is ByteArray -> byteArrayPreferencesKey(key)
                else -> throw IllegalArgumentException("Unsupported storage type: ${value::class}")
            }
            @Suppress("UNCHECKED_CAST")
            preferences[preferencesKey as Preferences.Key<T>] = value
        }
    }

    @Throws(ClassCastException::class, IllegalArgumentException::class)
    override suspend fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T? {
        val preferenceKey = getPreferenceKey(key, clazz)
        val value = dataStore.data.first()[preferenceKey] ?: return null

        @Suppress("UNCHECKED_CAST")
        return value as T
    }

    @Throws(IllegalArgumentException::class)
    override suspend fun <T : Any> contains(key: String, clazz: KClass<T>): Boolean {
        val preferenceKey = getPreferenceKey(key, clazz)
        return dataStore.data.first().contains(preferenceKey)
    }

    private fun getPreferenceKey(key: String, clazz: KClass<*>): Preferences.Key<*> {
        return when (clazz) {
            String::class -> stringPreferencesKey(key)
            Int::class -> intPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            else -> throw IllegalArgumentException("Unsupported storage type: $clazz")
        }
    }
}
