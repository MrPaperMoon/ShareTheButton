package mrpapermoon.customebutton.repository

import kotlinx.coroutines.flow.Flow
import mrpapermoon.customebutton.entity.SharedButton

interface ButtonRepository {
    val buttons: Flow<List<SharedButton>>

    suspend fun add(button: SharedButton)
    suspend fun load()
    suspend fun share(buttonId: String): String
    suspend fun join(token: String): SharedButton
}
