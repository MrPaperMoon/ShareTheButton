package mrpapermoon.customebutton.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraphFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mrpapermoon.customebutton.SharedViewModel
import mrpapermoon.customebutton.data.ButtonRemoteDataSource
import mrpapermoon.customebutton.data.ButtonRepositoryImpl
import mrpapermoon.customebutton.data.SessionRepositoryImpl
import mrpapermoon.customebutton.data.database.ButtonDatabase
import mrpapermoon.customebutton.data.database.buildButtonDatabase
import mrpapermoon.customebutton.qr.QrGenerator
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.repository.ClipboardRepository
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.storage.LightDataStorage
import mrpapermoon.customebutton.ui.create.CreateButtonViewModel
import mrpapermoon.customebutton.ui.home.HomeViewModel
import mrpapermoon.customebutton.ui.share.ShareButtonViewModel
import mrpapermoon.customebutton.ui.welcome.WelcomeViewModel
import mrpapermoon.customebutton.usecase.ShareInvitationUseCase

/**
 * Composition root assembling repositories, data sources and presentation models
 * from platform services and shared implementations.
 *
 * The host supplies the Room builder, preferences, invitation backend, share action
 * and clipboard. A QR renderer can also be provided. The database and repositories
 * are scoped to the graph. AndroidX holds screen ViewModels for their owners, while
 * the assisted factories supply the button ID for Share and Action.
 *
 * @property sharedViewModel Startup model.
 * @property homeViewModel Button list model.
 * @property welcomeViewModel Guest entry model.
 * @property createButtonViewModel Creation wizard model.
 * @property shareButtonViewModelFactory Share factory.
 */
@DependencyGraph(AppScope::class)
interface AppGraph {
    val sharedViewModel: SharedViewModel
    val homeViewModel: HomeViewModel
    val welcomeViewModel: WelcomeViewModel
    val createButtonViewModel: CreateButtonViewModel
    val shareButtonViewModelFactory: ShareButtonViewModel.Factory

    /**
     * Provides the scoped local database from a platform-configured [builder].
     *
     * @throws IllegalArgumentException If the Room builder is misconfigured.
     */
    @Provides
    @SingleIn(AppScope::class)
    /**
     * @throws IllegalArgumentException If Room rejects incompatible builder options, such as conflicting migration
     *     configuration.
     * @throws IllegalStateException If the generated Room implementation cannot be constructed or required builder
     *     configuration is missing.
     */
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun database(builder: RoomDatabase.Builder<ButtonDatabase>): ButtonDatabase = buildButtonDatabase(builder)

    /** Provides the scoped button repository backed by [database] and [remote]. */
    @Provides
    @SingleIn(AppScope::class)
    fun buttons(database: ButtonDatabase, remote: ButtonRemoteDataSource): ButtonRepository =
        ButtonRepositoryImpl(database.buttons(), remote)

    /** Provides the scoped guest session repository using [storage] on the I/O dispatcher. */
    @Provides
    @SingleIn(AppScope::class)
    fun session(storage: LightDataStorage): SessionRepository = SessionRepositoryImpl(storage, Dispatchers.IO)

    /** Metro factory that accepts platform resources and the selected invitation backend. */
    @DependencyGraph.Factory
    fun interface Factory {
        /**
         * Creates a graph with the supplied storage and backend.
         *
         * @param databaseBuilder Builder for the main local button database.
         * @param preferences Preferences DataStore for the local session.
         * @param remote Backend used for invitation publication and lookup.
         * @param shareInvitation Platform share sheet action.
         * @param clipboard Platform clipboard repository.
         * @param qrGenerator Platform QR renderer, if available.
         * @return The configured application graph.
         */
        fun create(
            @Provides databaseBuilder: RoomDatabase.Builder<ButtonDatabase>,
            @Provides preferences: DataStore<Preferences>,
            @Provides remote: ButtonRemoteDataSource,
            @Provides shareInvitation: ShareInvitationUseCase,
            @Provides clipboard: ClipboardRepository,
            @Provides qrGenerator: QrGenerator
        ): AppGraph
    }
}

/**
 * Creates a graph through the generated Metro factory.
 *
 * @param databaseBuilder Builder that supplies the local database location.
 * @param preferences Preferences DataStore for the local session.
 * @param remote Invitation backend supplied by the platform or build variant.
 * @param shareInvitation Platform share sheet action.
 * @param clipboard Platform clipboard repository.
 * @param qrGenerator Platform QR renderer, if available.
 * @return The configured application graph.
 */
fun createAppGraph(
    databaseBuilder: RoomDatabase.Builder<ButtonDatabase>,
    preferences: DataStore<Preferences>,
    remote: ButtonRemoteDataSource,
    shareInvitation: ShareInvitationUseCase,
    clipboard: ClipboardRepository,
    qrGenerator: QrGenerator
): AppGraph = createGraphFactory<AppGraph.Factory>().create(
    databaseBuilder, preferences, remote, shareInvitation, clipboard, qrGenerator
)
