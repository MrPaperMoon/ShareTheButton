package mrpapermoon.customebutton.usecase

/**
 * Application action for handing an invitation to the platform share sheet.
 *
 * Platform adapters own message localization and share-sheet presentation.
 *
 * The caller supplies the published URI. The implementation formats the localized
 * message and asks the platform to present available destinations. Completion means
 * the presentation was requested and conveys no information about delivery.
 */
fun interface ShareInvitationUseCase {
    /**
     * Requests presentation of the system share sheet without waiting for the recipient's choice.
     *
     * @param link Invitation URI to include in the localized message.
     */
    suspend operator fun invoke(link: String)
}
