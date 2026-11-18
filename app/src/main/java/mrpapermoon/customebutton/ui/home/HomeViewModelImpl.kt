package mrpapermoon.customebutton.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import mrpapermoon.customebutton.navigation.NavigationActor
import mrpapermoon.customebutton.repository.ButtonRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    override val repository: ButtonRepository,
) : ViewModel(), HomeViewModel {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val viewState = MutableStateFlow(HomeViewState())

    init {
        postInit()
    }
}
