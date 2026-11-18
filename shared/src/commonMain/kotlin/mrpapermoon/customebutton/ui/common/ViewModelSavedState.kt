package mrpapermoon.customebutton.ui.common

interface ViewModelSavedState {
    operator fun <T> get(key: String): T?
    operator fun <T> set(key: String, value: T)
}
