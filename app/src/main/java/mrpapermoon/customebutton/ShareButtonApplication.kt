package mrpapermoon.customebutton

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import mrpapermoon.customebutton.data.createButtonRemoteDataSource
import mrpapermoon.customebutton.data.database.ButtonDatabase
import mrpapermoon.customebutton.di.createAppGraph
import mrpapermoon.customebutton.qr.ZxingQrGenerator
import mrpapermoon.customebutton.repository.AndroidClipboardRepository
import mrpapermoon.customebutton.usecase.AndroidShareInvitationUseCase

private val Context.lightDataStore by preferencesDataStore(
    name = "share_button",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, "share_button"))
    }
)

/**
 * Builds the Android application graph on first use.
 *
 * The app supplies the database path and preferences, then binds the invitation backend
 * for the current build variant and the Android share, clipboard and QR implementations.
 *
 * @property graph Application-scoped dependencies.
 */
class ShareButtonApplication : Application() {
    internal val graph by lazy {
        createAppGraph(
            databaseBuilder = Room.databaseBuilder<ButtonDatabase>(
                this,
                getDatabasePath("buttons.db").absolutePath
            ),
            preferences = lightDataStore,
            remote = createButtonRemoteDataSource(this),
            shareInvitation = AndroidShareInvitationUseCase(this),
            clipboard = AndroidClipboardRepository(this),
            qrGenerator = ZxingQrGenerator()
        )
    }
}
