package mrpapermoon.customebutton.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.SharedViewModel
import mrpapermoon.customebutton.data.database.ButtonEntity
import mrpapermoon.customebutton.data.database.toEntity
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.ui.create.CreateButtonEvent
import mrpapermoon.customebutton.ui.create.CreateButtonStep
import mrpapermoon.customebutton.ui.create.CreateButtonViewModel
import mrpapermoon.customebutton.ui.create.draft.DraftButtonEvent
import mrpapermoon.customebutton.ui.home.HomeViewModel
import mrpapermoon.customebutton.ui.welcome.WelcomeEnterMode
import mrpapermoon.customebutton.ui.welcome.WelcomeEvent
import mrpapermoon.customebutton.ui.welcome.WelcomeViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class ScreenViewModelsTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private var nextKey = 0

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    private fun fill(vm: CreateButtonViewModel) {
        vm.onEvent(DraftButtonEvent.TitleChanged(" Miss you "))
        vm.onEvent(DraftButtonEvent.ActionChanged("SOS"))
        vm.onEvent(CreateButtonEvent.SelectStep(CreateButtonStep.Confirm))
        vm.onEvent(DraftButtonEvent.NotificationChanged("I miss you!"))
    }

    @Test
    fun savesOnceAndCompletesOnlyAfterSuccess() = runTest(dispatcher) {
        val repository = FakeButtons()
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        val vm = retain(CreateButtonViewModel(repository))
        fill(vm)
        vm.onEvent(CreateButtonEvent.Save)
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertFalse(vm.viewState.value.enabled)
        assertNull(vm.viewState.value.createdButtonId)
        assertEquals(1, repository.calls)
        gate.complete(Unit)
        runCurrent()
        assertNotNull(vm.viewState.value.createdButtonId)
        assertEquals("_Miss_you_", repository.buttons.value.single().title)
    }

    // Deferred until save-error recovery is implemented. The draft must survive a retry.
    @Ignore
    @Test
    fun failedSaveKeepsDraftForRetry() = runTest(dispatcher) {
        val repository = FakeButtons().apply { fail = true }
        val vm = retain(CreateButtonViewModel(repository))
        fill(vm)
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertNull(vm.viewState.value.createdButtonId)
        assertEquals(validDraft(), vm.draftState.value)
        assertNotNull(vm.viewState.value.error)
        repository.fail = false
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(1, repository.buttons.value.size)
        assertNull(vm.viewState.value.error)
    }

    @Test
    fun invalidDraftDoesNotSaveAndNewCreatorStartsEmpty() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = retain(CreateButtonViewModel(repository))
        fill(vm)
        vm.onEvent(DraftButtonEvent.TitleChanged(""))
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(0, repository.calls)
        val reopened = retain(CreateButtonViewModel(repository))
        assertEquals("", reopened.draftState.value.title)
    }

    @Test
    fun guestEntryIsPersistedAndUpdatesState() = runTest(dispatcher) {
        val session = FakeSession()
        val vm = retain(WelcomeViewModel(session))
        runCurrent()
        vm.onEvent(WelcomeEvent.SignIn(WelcomeEnterMode.GUEST))
        runCurrent()
        assertTrue(session.entered)
        assertTrue(vm.viewState.value.entered)
    }

    @Test
    fun homeReflectsRepository() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = retain(HomeViewModel(repository))
        runCurrent()
        val button = SharedButton("id", Color(0xFF9DE0C3), "Title", "Action", "Notice")
        repository.buttons.value = listOf(button)
        runCurrent()
        assertEquals(listOf(button), vm.viewState.value.buttons)
    }


    @Test
    fun startupWaitsForSessionAndSelectsReturningUser() = runTest(dispatcher) {
        val gate = CompletableDeferred<Boolean>()
        val session = object : SessionRepository {
            override suspend fun hasSession() = gate.await()
            override suspend fun enterAsGuest() = Unit
        }
        val vm = retain(SharedViewModel(session))
        runCurrent()
        assertNull(vm.isLoggedIn.value)
        gate.complete(true)
        runCurrent()
        assertEquals(true, vm.isLoggedIn.value)
    }

    @Test
    fun newSessionStartsAtWelcome() = runTest(dispatcher) {
        val vm = retain(SharedViewModel(FakeSession()))
        runCurrent()
        assertEquals(false, vm.isLoggedIn.value)
    }

    @Test
    fun welcomeDoesNotReadSession() = runTest(dispatcher) {
        val session = object : SessionRepository {
            override suspend fun hasSession(): Boolean = error("Welcome must not read session")
            override suspend fun enterAsGuest() = Unit
        }
        val vm = retain(WelcomeViewModel(session))
        runCurrent()
        assertFalse(vm.viewState.value.entered)
        vm.onEvent(WelcomeEvent.SignIn(WelcomeEnterMode.GUEST))
        runCurrent()
        assertTrue(vm.viewState.value.entered)
    }

    @Test
    fun clearingViewModelCancelsStartupRead() = runTest(dispatcher) {
        var cancelled = false
        val session = object : SessionRepository {
            override suspend fun hasSession(): Boolean {
                try {
                    kotlinx.coroutines.awaitCancellation()
                } finally {
                    cancelled = true
                }
            }

            override suspend fun enterAsGuest() = Unit
        }
        val vm = retain(SharedViewModel(session))
        runCurrent()
        store.clear()
        runCurrent()
        assertTrue(cancelled)
        assertNull(vm.isLoggedIn.value)
    }

    private fun <T : ViewModel> retain(viewModel: T): T =
        viewModel.also { store.put((nextKey++).toString(), it) }

    private fun validDraft() =
        ButtonUiState(Color(0xFFF4A7C1), "_Miss_you_", "SOS", "I_miss_you!")

    private class FakeButtons : ButtonRepository {
        override val buttons = MutableStateFlow<List<SharedButton>>(emptyList())
        var calls = 0
        var fail = false
        var gate: CompletableDeferred<Unit>? = null
        override suspend fun get(id: String): ButtonEntity? = buttons.value.find { it.id == id }?.toEntity()
        override suspend fun load() {}
        override suspend fun share(buttonId: String): String = error("Not used in screen tests")
        override suspend fun join(token: String): SharedButton = error("Not used in screen tests")
        override suspend fun add(button: ButtonEntity) {
            calls++
            gate?.await()
            if (fail) error("Disk unavailable")
            buttons.value += button.toButton()
        }
    }

    private class FakeSession : SessionRepository {
        var entered = false
        override suspend fun hasSession() = entered
        override suspend fun enterAsGuest() {
            entered = true
        }
    }
}
