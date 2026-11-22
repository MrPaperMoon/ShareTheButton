package mrpapermoon.customebutton.logging

import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("CustomeButton")

actual fun logNonFatal(error: Throwable, getMessage: () -> String) {
    val message = getMessage()
    logger.error(message, error)
    runCatching {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log(message)
        crashlytics.recordException(error)
    }.onFailure { logger.warn("Could not record the failure in Crashlytics", it) }
}

actual fun logBreadcrumb(getMessage: () -> String) {
    val message = getMessage()
    logger.info(message)
    runCatching {
        FirebaseCrashlytics.getInstance().log(message)
    }.onFailure { logger.warn("Could not record the breadcrumb in Crashlytics", it) }
}
