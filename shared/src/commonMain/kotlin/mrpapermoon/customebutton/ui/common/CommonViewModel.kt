package mrpapermoon.customebutton.ui.common

import kotlinx.coroutines.CoroutineScope

interface CommonViewModel {
    val coroutineScope: CoroutineScope
        get() = TODO("Provide platform specific coroutine scope")
}
