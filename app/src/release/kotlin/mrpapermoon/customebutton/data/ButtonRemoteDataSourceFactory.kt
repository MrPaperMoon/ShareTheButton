package mrpapermoon.customebutton.data

import android.content.Context

/**
 * Selects Firebase as the invitation backend for release builds.
 *
 * @param context Application context supplied by the Android entry point.
 * @return The default Firebase-backed invitation source.
 */
internal fun createButtonRemoteDataSource(context: Context): ButtonRemoteDataSource =
    createFirestoreButtonDataSource()
