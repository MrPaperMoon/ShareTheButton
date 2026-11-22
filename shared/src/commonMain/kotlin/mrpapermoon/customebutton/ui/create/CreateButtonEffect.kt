package mrpapermoon.customebutton.ui.create

/**
 * Presentation feedback describing invalid input or a failed local save.
 */
sealed interface CreateButtonEffect {

    sealed interface Error : CreateButtonEffect
    data object InvalidInputData : Error
    data object ButtonAlreadyExist : Error
    data object SQLiteException : Error
}
