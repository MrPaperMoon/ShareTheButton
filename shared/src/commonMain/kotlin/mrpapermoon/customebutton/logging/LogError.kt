package mrpapermoon.customebutton.logging

/**
 * Records a handled failure with a short description of the operation that failed.
 * Android also attaches the failure to Firebase Crashlytics as a non-fatal event.
 */
expect fun logNonFatal(error: Throwable, getMessage: () -> String)

fun logNonFatal(error: Throwable) {
    logNonFatal(error) { error.message.orEmpty() }
}

@Suppress("NOTHING_TO_INLINE")
inline fun logNonFatal(getMessage: () -> String) {
    val message = getMessage()
    logNonFatal(Exception(message)) { message }
}

internal expect fun logBreadcrumb(getMessage: () -> String)
