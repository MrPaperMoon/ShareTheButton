package mrpapermoon.customebutton

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import mrpapermoon.customebutton.repository.SessionRepository
import javax.inject.Inject

@HiltViewModel
class SharedViewModelImpl @Inject constructor(
    override val sessionRepository: SessionRepository
) : ViewModel(), SharedViewModel {

    override val coroutineScope: CoroutineScope
        get() = viewModelScope

    override val isLoggedIn = MutableStateFlow<Boolean?>(null)
}
