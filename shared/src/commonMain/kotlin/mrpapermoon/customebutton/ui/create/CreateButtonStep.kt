package mrpapermoon.customebutton.ui.create

/** Ordered steps of the button creation wizard. */
enum class CreateButtonStep {
    /** Enter the title, action label and notification text. */
    Text,
    /** Choose the button color. */
    Color,
    /** Review the draft and save it. */
    Confirm
}
