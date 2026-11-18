package mrpapermoon.customebutton.ui.action

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import mrpapermoon.customebutton.repository.ButtonRepository
import javax.inject.Inject

@HiltViewModel
class ActionViewModelImpl @Inject constructor(
    repository: ButtonRepository,
) : ViewModel(), ActionViewModelContract by ActionViewModel(repository) {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope
}
