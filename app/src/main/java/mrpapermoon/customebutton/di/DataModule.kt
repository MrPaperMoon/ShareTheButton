package mrpapermoon.customebutton.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import mrpapermoon.customebutton.data.ButtonRemoteDataSource
import mrpapermoon.customebutton.data.ButtonRepositoryImpl
import mrpapermoon.customebutton.data.FirestoreButtonDataSource
import mrpapermoon.customebutton.data.LightDataStorageImpl
import mrpapermoon.customebutton.data.SessionRepositoryImpl
import mrpapermoon.customebutton.data.database.ButtonDatabase
import mrpapermoon.customebutton.data.database.buildButtonDatabase
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.storage.LightDataStorage

private val Context.lightDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "share_button",
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, "share_button"))
    }
)

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
internal abstract class StorageModule {
    @Binds
    @Singleton
    abstract fun remote(implementation: FirestoreButtonDataSource): ButtonRemoteDataSource

    @Binds
    @Singleton
    abstract fun storage(implementation: LightDataStorageImpl): LightDataStorage
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun dataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.lightDataStore

    @Provides
    @Singleton
    fun buttons(
        database: ButtonDatabase,
        remote: ButtonRemoteDataSource
    ): ButtonRepository = ButtonRepositoryImpl(database.buttons(), remote)

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): ButtonDatabase = buildButtonDatabase(
        Room.databaseBuilder<ButtonDatabase>(context, context.getDatabasePath("buttons.db").absolutePath)
    )

    @Provides
    @Singleton
    fun firestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun session(storage: LightDataStorage, @IoDispatcher dispatcher: CoroutineDispatcher): SessionRepository =
        SessionRepositoryImpl(storage, dispatcher)

    @Provides
    @IoDispatcher
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
