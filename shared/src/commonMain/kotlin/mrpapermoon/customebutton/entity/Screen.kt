package mrpapermoon.customebutton.entity

/**
 * Identifies a destination and supplies its concrete navigation path.
 *
 * [Share] encode a button ID in the path and expose a separate route
 * pattern for registration. The home destination requests single-top navigation.
 *
 * @property path Concrete destination path.
 * @property singleInstance Single-top navigation flag.
 */
@Suppress("ConstPropertyName")
sealed class Screen(val path: String) {
    open val singleInstance = false

    /** Root destination that displays guest entry or the local button list. */
    data object Home : Screen("home") {
        /** Uses single-top navigation for the root destination. */
        override val singleInstance: Boolean
            get() = true
    }

    /** Invitation screen for a local button identified by [buttonId]. */
    data class Share(val buttonId: String) : Screen("share/$buttonId") {
        companion object {
            /** Share destination pattern with a `buttonId` argument. */
            const val route = "share/{buttonId}"
        }
    }

    /** Destination for the button editor. */
    data object Create : Screen("create")
}
