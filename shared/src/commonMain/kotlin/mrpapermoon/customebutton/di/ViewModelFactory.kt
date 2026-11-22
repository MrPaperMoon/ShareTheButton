package mrpapermoon.customebutton.di

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

/**
 * Creates the AndroidX factory used at the Activity and navigation destinations.
 *
 * Share and Action need a `buttonId` argument in the destination saved state.
 *
 * @receiver Application graph that supplies ViewModels and their dependencies.
 * @return A factory that creates the model requested by its ViewModel store.
 */
fun AppGraph.viewModelFactory() = viewModelFactory {
    initializer { sharedViewModel }
    initializer { homeViewModel }
    initializer { welcomeViewModel }
    initializer { createButtonViewModel }
    initializer { shareButtonViewModelFactory.create(checkNotNull(createSavedStateHandle()["buttonId"])) }
}
