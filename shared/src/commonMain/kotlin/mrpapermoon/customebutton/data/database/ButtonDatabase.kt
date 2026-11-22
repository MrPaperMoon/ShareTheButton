package mrpapermoon.customebutton.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * Room infrastructure defining the local database schema and its DAO entry point.
 *
 * The platform supplies the storage location to [buildButtonDatabase]. The shared
 * builder configures the bundled SQLite driver. The debug invitation backend uses its
 * own database so publishing a button does not change the contents of this table.
 */
@Database(entities = [ButtonEntity::class], version = 1)
@ConstructedBy(ButtonDatabaseConstructor::class)
abstract class ButtonDatabase : RoomDatabase() {
    /** Returns the DAO for the local buttons table. */
    abstract fun buttons(): ButtonDao
}

/** Room-generated constructor for each supported target. */
expect object ButtonDatabaseConstructor : RoomDatabaseConstructor<ButtonDatabase> {
    /** Creates the generated [ButtonDatabase] implementation for the current target. */
    override fun initialize(): ButtonDatabase
}

/**
 * Builds a local Room database with bundled SQLite and the I/O dispatcher.
 *
 * @param builder Platform-configured Room builder with the database location.
 * @return The configured database instance.
 * @throws IllegalArgumentException If the Room builder is misconfigured.
 * @throws IllegalStateException If the generated Room implementation cannot be constructed or required builder
 *     configuration is missing.
 */
@Throws(IllegalArgumentException::class, IllegalStateException::class)
fun buildButtonDatabase(builder: RoomDatabase.Builder<ButtonDatabase>): ButtonDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()
