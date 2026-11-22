package mrpapermoon.customebutton.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mrpapermoon.customebutton.data.database.ButtonEntity
import mrpapermoon.customebutton.data.database.toEntity
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.ui.create.CreateButtonEvent
import mrpapermoon.customebutton.ui.create.CreateButtonStep
import mrpapermoon.customebutton.ui.create.CreateButtonViewModel
import mrpapermoon.customebutton.ui.create.draft.DraftButtonEvent
import mrpapermoon.customebutton.ui.home.HomeViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelLifecycleTest {
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
    fun clearingViewModelCancelsRepositoryCollector() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = HomeViewModel(repository)
        store.put("home", vm)
        runCurrent()
        assertEquals(1, repository.buttons.subscriptionCount.value)
        store.clear()
        runCurrent()
        assertEquals(0, repository.buttons.subscriptionCount.value)
    }

    @Test
    fun creatorKeepsDraftAcrossStepsAndSavesOnConfirmation() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = CreateButtonViewModel(repository)
        store.put("create", vm)
        assertEquals(CreateButtonStep.Text, vm.viewState.value.step)
        vm.onEvent(CreateButtonEvent.Previous)
        vm.onEvent(DraftButtonEvent.ColorChanged(Color(0xFF123456)))
        vm.onEvent(CreateButtonEvent.Next)
        assertEquals(CreateButtonStep.Color, vm.viewState.value.step)
        vm.onEvent(DraftButtonEvent.TitleChanged("Miss you"))
        vm.onEvent(DraftButtonEvent.ActionChanged("SOS"))
        vm.onEvent(DraftButtonEvent.NotificationChanged("Hello"))
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Color))
        assertEquals("Miss_you", vm.draftState.value.title)
        assertEquals(0, repository.buttons.value.size)
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Confirm))
        vm.onEvent(CreateButtonEvent.Save)
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(1, repository.buttons.value.size)
        assertEquals(Color(0xFF123456), repository.buttons.value.single().color)
        assertEquals(false, vm.viewState.value.enabled)
        assertEquals(repository.buttons.value.single().id, vm.viewState.value.createdButtonId)
    }

    @Test
    fun confirmingEmptyDraftDoesNotSave() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = CreateButtonViewModel(repository)
        store.put("create", vm)
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Confirm))
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(0, repository.buttons.value.size)
        assertEquals(true, vm.viewState.value.enabled)
        assertEquals(null, vm.viewState.value.createdButtonId)
    }

    private class FakeButtons : ButtonRepository {
        override val buttons = MutableStateFlow<List<SharedButton>>(emptyList())
        override suspend fun add(button: ButtonEntity) {
            buttons.value += button.toButton()
        }

        override suspend fun get(id: String): ButtonEntity? = buttons.value.find { it.id == id }?.toEntity()
        override suspend fun load() = Unit
        override suspend fun share(buttonId: String): String = error("Not used")
        override suspend fun join(token: String): SharedButton = error("Not used")
    }
}
