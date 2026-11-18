package mrpapermoon.customebutton.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import mrpapermoon.customebutton.repository.ButtonRepository
import javax.inject.Inject

@HiltViewModel
class CreateButtonViewModelImpl private constructor(
    private val delegate: CreateButtonViewModel
) : ViewModel(), CreateButtonViewModelContract by delegate {
    @Inject
    constructor(repository: ButtonRepository) : this(CreateButtonViewModel(repository))

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    init {
        delegate.coroutineScope = coroutineScope
    }
}
