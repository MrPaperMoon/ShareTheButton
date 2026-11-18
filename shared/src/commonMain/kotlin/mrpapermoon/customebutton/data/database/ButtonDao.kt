package mrpapermoon.customebutton.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ButtonDao {
    @Query("SELECT * FROM buttons ORDER BY rowid")
    fun observeAll(): Flow<List<ButtonEntity>>

    @Query("SELECT * FROM buttons ORDER BY rowid")
    suspend fun getAll(): List<ButtonEntity>

    @Query("SELECT * FROM buttons WHERE id = :id")
    suspend fun find(id: String): ButtonEntity?

    @Insert
    suspend fun insert(button: ButtonEntity)

    @Upsert
    suspend fun upsert(button: ButtonEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun importLegacy(buttons: List<ButtonEntity>)
}
