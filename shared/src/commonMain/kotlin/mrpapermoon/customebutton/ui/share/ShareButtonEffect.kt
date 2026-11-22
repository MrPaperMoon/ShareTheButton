package mrpapermoon.customebutton.ui.share

/**
 * Presentation effects emitted after an invitation action, such as clipboard confirmation.
 */
sealed interface ShareButtonEffect {
    data object OnCopyLink: ShareButtonEffect
}
