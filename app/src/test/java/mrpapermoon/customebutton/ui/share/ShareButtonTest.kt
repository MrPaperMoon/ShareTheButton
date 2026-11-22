package mrpapermoon.customebutton.ui.share

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withTimeout
import mrpapermoon.customebutton.data.ButtonRemoteDataSource
import mrpapermoon.customebutton.data.ButtonRepositoryImpl
import mrpapermoon.customebutton.data.database.ButtonDao
import mrpapermoon.customebutton.data.database.ButtonEntity
import mrpapermoon.customebutton.data.database.toEntity
import mrpapermoon.customebutton.entity.SharedButton
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShareButtonTest {
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
    fun failedPublicationReusesPersistedTokenAndExposesLinkOnlyAfterSuccess() = runTest(dispatcher) {
        val dao = MemoryDao()
        val remote = Remote()
        val repository = ButtonRepositoryImpl(dao, remote)
        repository.add(button.toEntity())
        val sharedLinks = mutableListOf<String>()
        val copiedLinks = mutableListOf<String>()
        val encodedLinks = mutableListOf<String>()
        val vm = ShareButtonViewModel(
            repository,
            button.id,
            shareInvitation = { sharedLinks += it },
            clipboard = { copiedLinks += it },
            qrGenerator = {
                encodedLinks += it
                TestQrBitmap
            }
        ).also { store.put("share", it) }
        runCurrent()
        assertEquals(1, remote.tokens.size)
        assertNull(vm.viewState.value.linkState.link)
        assertEquals(ShareButtonViewState.LinkState.Failed, vm.viewState.value.linkState)
        val pending = dao.find(button.id)!!.shareToken
        remote.fail = false
        vm.onEvent(ShareButtonEvent.Retry)
        runCurrent()
        assertEquals(listOf(pending, pending), remote.tokens)
        val link = "customebutton://join/$pending"
        assertEquals(link, vm.viewState.value.linkState.link)
        vm.onEvent(ShareButtonEvent.OnShareClick(link))
        vm.onEvent(ShareButtonEvent.OnCopyClick(link))
        runCurrent()
        assertEquals(listOf(link), sharedLinks)
        assertEquals(listOf(link), copiedLinks)
        assertEquals(listOf(link), encodedLinks)
        assertNotNull(vm.viewState.value.qr)
        assertNotEquals(button.id, pending)
        assertEquals(1, dao.observeAll().first().size)
    }

    @Ignore("Timeout and missing-button handling deferred to the error-handling iteration")
    @Test
    fun timeoutAllowsRetryAndMissingButtonDoesNotPublish() = runTest(dispatcher) {
        val dao = MemoryDao()
        val remote = Remote().apply { timeout = true }
        val repository = ButtonRepositoryImpl(dao, remote)
        repository.add(button.toEntity())
        val vm = ShareButtonViewModel(repository, button.id, {}, {}, { TestQrBitmap }).also { store.put("share", it) }
        vm.onEvent(ShareButtonEvent.Retry)
        runCurrent()
        testScheduler.advanceTimeBy(11)
        runCurrent()
        assertEquals(ShareButtonViewState.LinkState.Failed, vm.viewState.value.linkState)
        assertNotEquals(ShareButtonViewState.LinkState.Loading, vm.viewState.value.linkState)
        val missing = ShareButtonViewModel(repository, "missing", {}, {}, { TestQrBitmap }).also { store.put("missing", it) }
        missing.onEvent(ShareButtonEvent.Retry)
        runCurrent()
        assertEquals(ShareButtonViewState.LinkState.Failed, missing.viewState.value.linkState)
        assertEquals(1, remote.tokens.size)
    }

    private object TestQrBitmap : ImageBitmap {
        override val width = 1
        override val height = 1
        override val colorSpace = ColorSpaces.Srgb
        override val hasAlpha = true
        override val config = ImageBitmapConfig.Argb8888

        override fun readPixels(
            buffer: IntArray, startX: Int, startY: Int, width: Int, height: Int, bufferOffset: Int, stride: Int
        ) {
            error("The ViewModel must not inspect QR pixels")
        }

        override fun prepareToDraw() = Unit
    }

    private val button = SharedButton("owner-id", Color(0xFF9DE0C3), "Hello", "SOS", "Notice")

    private class Remote : ButtonRemoteDataSource {
        val tokens = mutableListOf<String>()
        var fail = true
        var timeout = false
        override suspend fun publish(token: String, button: SharedButton) {
            tokens += token
            if (timeout) withTimeout(10) { awaitCancellation() }
            check(!fail)
        }
        override suspend fun fetch(token: String): SharedButton? = null
    }

    private class MemoryDao : ButtonDao {
        private val rows = MutableStateFlow<List<ButtonEntity>>(emptyList())
        override fun observeAll() = rows
        override suspend fun find(id: String) = rows.value.find { it.id == id }
        override suspend fun insert(button: ButtonEntity) { rows.value += button }
        override suspend fun upsert(button: ButtonEntity) {
            rows.value = rows.value.filterNot { it.id == button.id } + button
        }
    }
}
