package mrpapermoon.customebutton.data

import mrpapermoon.customebutton.entity.SharedButton

interface ButtonRemoteDataSource {
    suspend fun publish(token: String, button: SharedButton)
    suspend fun fetch(token: String): SharedButton?
}
