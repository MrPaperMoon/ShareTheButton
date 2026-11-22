package mrpapermoon.customebutton.logging

import org.apache.logging.log4j.LogManager

private val logger = LogManager.getLogger("CustomeButton")

actual fun logNonFatal(error: Throwable, getMessage: () -> String) {
    logger.error(getMessage, error)
}

actual fun logBreadcrumb(getMessage: () -> String) {
    logger.info(getMessage)
}
