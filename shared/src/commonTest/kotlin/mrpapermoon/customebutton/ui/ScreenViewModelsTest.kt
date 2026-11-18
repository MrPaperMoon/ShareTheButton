package mrpapermoon.customebutton.ui

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import mrpapermoon.customebutton.ui.common.CommonViewModel
import mrpapermoon.customebutton.ui.common.ViewModelSavedState
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.SharedViewModel
import mrpapermoon.customebutton.entity.ButtonColor
import mrpapermoon.customebutton.entity.SharedButton
import mrpapermoon.customebutton.repository.ButtonRepository
import mrpapermoon.customebutton.repository.SessionRepository
import mrpapermoon.customebutton.ui.action.*
import mrpapermoon.customebutton.ui.create.*
import mrpapermoon.customebutton.ui.home.*
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.welcome.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ScreenViewModelsTest {
    private val dispatcher = StandardTestDispatcher()

    private fun fill(vm: CreateButtonViewModel) {
        vm.onEvent(CreateButtonEvent.TitleChanged(" Р РЋР С”РЎС“РЎвЂЎР В°РЎР‹ "))
        vm.onEvent(CreateButtonEvent.ActionChanged("SOS"))
        vm.onEvent(CreateButtonEvent.NotificationChanged("Р Р‡ РЎРѓР С”РЎС“РЎвЂЎР В°РЎР‹!"))
    }

    @Test
    fun savesOnceAndCompletesOnlyAfterSuccess() = runTest(dispatcher) {
        val repository = FakeButtons()
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        val vm = attach(CreateButtonViewModel(repository, MemorySavedState()))
        fill(vm)
        vm.onEvent(CreateButtonEvent.Save)
        vm.onEvent(CreateButtonEvent.Save)
        vm.onEvent(CreateButtonEvent.Cancel)
        runCurrent()
        assertTrue(vm.viewState.value.saving)
        assertFalse(vm.viewState.value.completed)
        assertEquals(1, repository.calls)
        gate.complete(Unit)
        runCurrent()
        assertTrue(vm.viewState.value.completed)
        assertEquals("Р РЋР С”РЎС“РЎвЂЎР В°РЎР‹", repository.buttons.value.single().title)
    }

    @Test
    fun failedSaveKeepsDraftForRetry() = runTest(dispatcher) {
        val repository = FakeButtons().apply { fail = true }
        val vm = attach(CreateButtonViewModel(repository, MemorySavedState()))
        fill(vm)
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertFalse(vm.viewState.value.completed)
        assertEquals(validDraft(), vm.viewState.value.draft)
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
        val vm = attach(CreateButtonViewModel(repository, MemorySavedState()))
        fill(vm)
        vm.onEvent(CreateButtonEvent.TitleChanged("   "))
        vm.onEvent(CreateButtonEvent.Save)
        runCurrent()
        assertEquals(0, repository.calls)
        vm.onEvent(CreateButtonEvent.Cancel)
        val reopened = attach(CreateButtonViewModel(repository, MemorySavedState()))
        assertEquals("", reopened.viewState.value.draft.title)
    }

    @Test
    fun restoresDraftFromSavedState() = runTest(dispatcher) {
        val saved = MemorySavedState()
        val vm = attach(CreateButtonViewModel(FakeButtons(), saved))
        fill(vm)
        val restored = MemorySavedState(saved.keys().associateWith { saved.get<Any?>(it) })
        val recreated = attach(CreateButtonViewModel(FakeButtons(), restored))
        assertEquals(validDraft(), recreated.viewState.value.draft)
    }

    @Test
    fun guestEntryIsPersistedAndUpdatesState() = runTest(dispatcher) {
        val session = FakeSession()
        val vm = attach(WelcomeViewModel(session))
        runCurrent()
        vm.onEvent(WelcomeEvent.Skip)
        runCurrent()
        assertTrue(session.entered)
        assertTrue(vm.viewState.value.entered)
    }

    @Test
    fun homeReflectsRepository() = runTest(dispatcher) {
        val repository = FakeButtons()
        val vm = attach(HomeViewModel(repository))
        runCurrent()
        val button = SharedButton("id", ButtonColor.Mint, "Title", "Action", "Notice")
        repository.buttons.value = listOf(button)
        runCurrent()
        assertEquals(listOf(button), vm.viewState.value.buttons)
    }

    @Test
    fun actionRestoresSelectionAndHandlesNoticeEvents() = runTest(dispatcher) {
        val repository = FakeButtons()
        val button = SharedButton("id", ButtonColor.Mint, "Title", "Action", "Notice")
        repository.buttons.value = listOf(button)
        val saved = MemorySavedState(mapOf("buttonId" to "id"))
        val vm = attach(ActionViewModel(repository, saved))
        runCurrent()
        assertEquals(button, vm.viewState.value.button)
        vm.onEvent(ActionEvent.Press)
        assertTrue(vm.viewState.value.showNotice)
        assertEquals(true, saved.get<Boolean>("showNotice"))
        vm.onEvent(ActionEvent.DismissNotice)
        assertFalse(vm.viewState.value.showNotice)
    }

    @Test
    fun sharedNavigationDeliversRepeatedEventsToSubscriber() = runTest(dispatcher) {
        val vm = attach(SharedViewModel(FakeSession()))
        val received = mutableListOf<NavigationEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.navigation.collect { received += it }
        }
        vm.onEvent(NavigationEvent.Create)
        vm.onEvent(NavigationEvent.Back)
        vm.onEvent(NavigationEvent.Create)
        runCurrent()
        assertEquals(listOf(NavigationEvent.Create, NavigationEvent.Back, NavigationEvent.Create), received)
    }

    @Test
    fun startupWaitsForSessionAndSelectsReturningUser() = runTest(dispatcher) {
        val gate = CompletableDeferred<Boolean>()
        val session = object : SessionRepository {
            override suspend fun hasSession() = gate.await()
            override suspend fun enterAsGuest() = Unit
        }
        val vm = attach(SharedViewModel(session))
        vm.postInit()
        runCurrent()
        assertNull(vm.isLoggedIn.value)
        gate.complete(true)
        runCurrent()
        assertEquals(true, vm.isLoggedIn.value)
    }

    @Test
    fun newSessionStartsAtWelcome() = runTest(dispatcher) {
        val vm = attach(SharedViewModel(FakeSession()))
        vm.postInit()
        runCurrent()
        assertEquals(false, vm.isLoggedIn.value)
    }

    @Test
    fun welcomeDoesNotReadSession() = runTest(dispatcher) {
        val session = object : SessionRepository {
            override suspend fun hasSession(): Boolean = error("Welcome must not read session")
            override suspend fun enterAsGuest() = Unit
        }
        val vm = attach(WelcomeViewModel(session))
        runCurrent()
        assertFalse(vm.viewState.value.entered)
        vm.onEvent(WelcomeEvent.Skip)
        runCurrent()
        assertTrue(vm.viewState.value.entered)
    }

    @Test
    fun attachingTwiceDoesNotDuplicateCollectorsAndCancellationStopsUpdates() = runTest(dispatcher) {
        val owner = CoroutineScope(SupervisorJob() + dispatcher)
        val repository = FakeButtons()
        val vm = HomeViewModel(repository)
        vm.attach(owner)
        vm.attach(owner)
        runCurrent()
        assertEquals(1, repository.buttons.subscriptionCount.value)
        assertSame(owner, vm.coroutineScope)
        owner.cancel()
        runCurrent()
        assertEquals(0, repository.buttons.subscriptionCount.value)
        repository.buttons.value = listOf(SharedButton("late", ButtonColor.Mint, "Hi", "SOS", "Hello"))
        runCurrent()
        assertTrue(vm.viewState.value.buttons.isEmpty())
        assertFailsWith<IllegalStateException> { vm.attach(backgroundScope) }
    }

    @Test
    fun cancellingPlatformScopeCancelsStartupRead() = runTest(dispatcher) {
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
        val owner = CoroutineScope(SupervisorJob() + dispatcher)
        val vm = SharedViewModel(session)
        vm.attach(owner)
        vm.postInit()
        runCurrent()
        owner.cancel()
        runCurrent()
        assertTrue(cancelled)
        assertNull(vm.isLoggedIn.value)
    }

    private fun <T : CommonViewModel> TestScope.attach(viewModel: T): T =
        viewModel.also { it.attach(backgroundScope) }

    private class MemorySavedState(initial: Map<String, Any?> = emptyMap()) : ViewModelSavedState {
        private val values = initial.toMutableMap()

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: String): T? = values[key] as T?

        override fun <T> set(key: String, value: T) {
            values[key] = value
        }

        fun keys(): Set<String> = values.keys
    }

    private fun validDraft() =
        ButtonUiState(ButtonColor.Pink, " Р РЋР С”РЎС“РЎвЂЎР В°РЎР‹ ", "SOS", "Р Р‡ РЎРѓР С”РЎС“РЎвЂЎР В°РЎР‹!")

    private class FakeButtons : ButtonRepository {
        override val buttons = MutableStateFlow<List<SharedButton>>(emptyList())
        var calls = 0
        var fail = false
        var gate: CompletableDeferred<Unit>? = null
        override suspend fun load() {}
        override suspend fun share(buttonId: String): String = error("Not used in screen tests")
        override suspend fun join(token: String): SharedButton = error("Not used in screen tests")
        override suspend fun add(button: SharedButton) {
            calls++
            gate?.await()
            if (fail) error("Disk unavailable")
            buttons.value += button
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
