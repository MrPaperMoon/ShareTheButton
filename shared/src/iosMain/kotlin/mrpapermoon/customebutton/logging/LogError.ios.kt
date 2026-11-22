package mrpapermoon.customebutton.logging

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLog

actual fun logNonFatal(error: Throwable, getMessage: () -> String) {
    NSLog("%@\n%@", getMessage(), error.stackTraceToString())
}

actual fun logBreadcrumb(getMessage: () -> String) {
    NSLog("%@", getMessage())
}