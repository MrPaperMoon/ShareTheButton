package mrpapermoon.customebutton.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import androidx.sqlite.SQLiteException
import kotlinx.coroutines.CancellationException
import mrpapermoon.customebutton.entity.SharedButton

/**
 * Opens the standalone Room database for debug invitations.
 *
 * @param context Context used to locate the database on the device.
 * @return A local invitation source for the debug variant.
 */
internal fun createButtonRemoteDataSource(context: Context): ButtonRemoteDataSource {
    val database = Room.databaseBuilder<StubButtonDatabase>(context.applicationContext, "button_remote_stub.db")
        .build()
    return StubButtonRemoteDataSource(database.buttons())
}

/**
 * Debug data-source adapter implementing [ButtonRemoteDataSource] with local Room storage.
 *
 * The source stores a snapshot under each token in a separate Room database. Publishing
 * again with the same token replaces that snapshot. [fetch] reads the result even
 * after the app has restarted.
 *
 * @param dao Debug publication DAO.
 */
internal class StubButtonRemoteDataSource(private val dao: RemoteButtonDao) : ButtonRemoteDataSource {
    /**
     * Inserts or replaces the snapshot of [button] under [token] in the debug database.
     *
     * @throws SQLiteException If SQLite cannot read or persist the button rows, including constraint violations or
     *     database I/O failures.
     * @throws IllegalStateException If Room cannot open or use the database, for example after closure or when its
     *     configuration is inconsistent.
     * @throws CancellationException If the caller cancels while waiting for the debug database operation.
     */
    @Throws(SQLiteException::class, IllegalStateException::class, CancellationException::class)
    override suspend fun publish(token: String, button: SharedButton) {
        dao.upsert(
            RemoteButtonEntity(
                token = token,
                id = button.id,
                color = button.color.toArgb().toUInt().toLong(),
                title = button.title,
                actionText = button.actionText,
                notificationText = button.notificationText
            )
        )
    }

    /**
     * Returns the button snapshot associated with [token], or `null` if absent.
     *
     * @throws SQLiteException If SQLite cannot read or persist the button rows, including constraint violations or
     *     database I/O failures.
     * @throws IllegalStateException If Room cannot open or use the database, for example after closure or when its
     *     configuration is inconsistent.
     * @throws CancellationException If the caller cancels while waiting for the debug database operation.
     */
    @Throws(SQLiteException::class, IllegalStateException::class, CancellationException::class)
    override suspend fun fetch(token: String): SharedButton? = dao.find(token)?.let {
        SharedButton(it.id, Color(it.color), it.title, it.actionText, it.notificationText)
    }
}

/** Debug publication row keyed by the invitation token and containing the published button snapshot. */
@Entity(tableName = "remote_buttons")
internal data class RemoteButtonEntity(
    @PrimaryKey val token: String,
    val id: String,
    val color: Long,
    val title: String,
    val actionText: String,
    val notificationText: String
)

/** Access to the separately stored debug publications. */
@Dao
internal interface RemoteButtonDao {
    /** Inserts [button] or replaces the existing row for that token. */
    @Upsert
    suspend fun upsert(button: RemoteButtonEntity)

    /** Returns the debug publication for [token], or `null` when absent. */
    @Query("SELECT * FROM remote_buttons WHERE token = :token")
    suspend fun find(token: String): RemoteButtonEntity?
}

/** Standalone Android debug database that emulates remote invitation storage. */
@Database(entities = [RemoteButtonEntity::class], version = 1, exportSchema = false)
internal abstract class StubButtonDatabase : RoomDatabase() {
    /** Returns the DAO for the `remote_buttons` table. */
    abstract fun buttons(): RemoteButtonDao
}
