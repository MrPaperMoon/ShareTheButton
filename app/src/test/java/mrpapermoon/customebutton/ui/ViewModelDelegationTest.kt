package mrpapermoon.customebutton.ui

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.ui.create.CreateButtonStep
import mrpapermoon.customebutton.ui.create.CreateButtonEvent
import mrpapermoon.customebutton.ui.create.CreateButtonViewModelImpl
import mrpapermoon.customebutton.ui.home.HomeViewModelImpl
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelDelegationTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun clearingAndroidViewModelCancelsCommonCollector() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = HomeViewModelImpl(repository)
        store.put("home", vm)
        assertSame(vm.viewModelScope, vm.coroutineScope)
        runCurrent()
        assertEquals(1, repository.buttons.subscriptionCount.value)
        store.clear()
        runCurrent()
        assertEquals(0, repository.buttons.subscriptionCount.value)
    }

    @Test
    fun creatorKeepsDraftAcrossAllNavigationPathsAndSavesOnlyOnConfirmation() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = CreateButtonViewModelImpl(repository)
        store.put("create", vm)
        assertEquals(CreateButtonStep.Color, vm.viewState.value.step)
        vm.onEvent(CreateButtonEvent.Previous)
        vm.onEvent(CreateButtonEvent.ColorChanged(ButtonColor.Custom(0xFF123456.toInt())))
        vm.onEvent(CreateButtonEvent.Next)
        assertEquals(CreateButtonStep.Brightness, vm.viewState.value.step)
        vm.onEvent(CreateButtonEvent.Next)
        vm.onEvent(CreateButtonEvent.Next)
        assertEquals(CreateButtonStep.Text, vm.viewState.value.step)
        vm.onEvent(CreateButtonEvent.TitleChanged("Miss you"))
        vm.onEvent(CreateButtonEvent.ActionChanged("SOS"))
        vm.onEvent(CreateButtonEvent.NotificationChanged("Hello"))
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Color))
        assertEquals("Miss you", vm.viewState.value.draft.title)
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(0, repository.buttons.value.size)
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Confirm))
        vm.onEvent(CreateButtonEvent.Save)
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(1, repository.buttons.value.size)
        assertEquals(ButtonColor.Custom(0xFF123456.toInt()), repository.buttons.value.single().color)
        assertEquals(true, vm.viewState.value.completed)
    }

    @Test
    fun confirmingEmptyDraftDoesNotSaveAndCancelCompletesWithoutWriting() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = CreateButtonViewModelImpl(repository)
        store.put("create", vm)
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Confirm))
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(0, repository.buttons.value.size)
        assertEquals(false, vm.viewState.value.completed)
        vm.onEvent(CreateButtonEvent.Cancel)
        assertEquals(true, vm.viewState.value.completed)
    }

    private class FakeButtons : ButtonRepository {
        override val buttons = MutableStateFlow<List<SharedButton>>(emptyList())
        override suspend fun add(button: SharedButton) {
            buttons.value += button
        }

        override suspend fun load() = Unit
        override suspend fun share(buttonId: String): String = error("Not used")
        override suspend fun join(token: String): SharedButton = error("Not used")
    }
}
