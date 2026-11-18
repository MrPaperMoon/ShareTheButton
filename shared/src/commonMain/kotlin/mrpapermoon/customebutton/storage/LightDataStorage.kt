package mrpapermoon.customebutton.storage

import kotlin.reflect.KClass

interface LightDataStorage {
    suspend fun <T : Any> put(key: String, value: T)
    suspend fun <T : Any> getOrNull(key: String, clazz: KClass<T>): T?
    suspend fun <T : Any> contains(key: String, clazz: KClass<T>): Boolean

    suspend fun getBoolean(key: String): Boolean = getOrNull(key, Boolean::class) ?: false
    suspend fun getString(key: String): String = getOrNull(key, String::class)!!
}
