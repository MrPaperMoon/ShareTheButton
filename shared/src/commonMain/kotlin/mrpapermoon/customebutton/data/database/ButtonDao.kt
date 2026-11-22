package mrpapermoon.customebutton.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

/**
 * Local data access boundary exposing SQL operations on [ButtonEntity] rows to repositories.
 *
 * [observeAll] emits database snapshots in row order. [find] is a one-time lookup.
 * [insert] requires a new primary key, while [upsert] is used when the repository needs
 * to store changes to an existing row, such as an invitation token.
 */
@Dao
interface ButtonDao {
    /** Returns a flow of all rows ordered by SQLite row ID. Query errors surface during collection. */
    @Query("SELECT * FROM buttons ORDER BY rowid")
    fun observeAll(): Flow<List<ButtonEntity>>

    /** Returns the row with the given [id], or `null` if it does not exist. */
    @Query("SELECT * FROM buttons WHERE id = :id")
    suspend fun find(id: String): ButtonEntity?

    /** Inserts [button] as a new row. A primary-key conflict aborts the insert. */
    @Insert
    suspend fun insert(button: ButtonEntity)

    /** Inserts [button] or updates the row with the same primary key. */
    @Upsert
    suspend fun upsert(button: ButtonEntity)
}
