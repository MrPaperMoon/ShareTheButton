package mrpapermoon.customebutton.repository

interface SessionRepository {
    suspend fun hasSession(): Boolean
    suspend fun enterAsGuest()
}
