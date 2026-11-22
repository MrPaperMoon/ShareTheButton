package mrpapermoon.customebutton.ui.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mrpapermoon.customebutton.LocalNavigationEventHandler
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.example_title
import mrpapermoon.customebutton.resources.example_action
import mrpapermoon.customebutton.resources.example_notification
import mrpapermoon.customebutton.resources.new_button
import mrpapermoon.customebutton.resources.back
import mrpapermoon.customebutton.resources.baseline_chevron_left_24
import mrpapermoon.customebutton.resources.ic_share_24
import mrpapermoon.customebutton.resources.retry
import mrpapermoon.customebutton.resources.share_button
import mrpapermoon.customebutton.resources.share_button_copied
import mrpapermoon.customebutton.resources.share_button_copy
import mrpapermoon.customebutton.resources.share_button_error
import mrpapermoon.customebutton.resources.share_button_link
import mrpapermoon.customebutton.resources.share_button_loading
import mrpapermoon.customebutton.resources.share_button_qr
import mrpapermoon.customebutton.resources.share_button_success
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.navigation.NavigationEvent
import mrpapermoon.customebutton.ui.share.ShareButtonViewState.LinkState
import mrpapermoon.customebutton.ui.theme.CustomeButtonTheme
import mrpapermoon.customebutton.widget.RoundedButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShareButtonScreen(viewModel: ShareButtonViewModel) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    ShareButtonScreen(state, viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareButtonScreen(
    state: ShareButtonViewState,
    eventHandler: EventHandler,
) {
    val navigation = LocalNavigationEventHandler.current

    val button = state.button
    val shareTitle = stringResource(Res.string.share_button)
    val shareButtonCopied = stringResource(Res.string.share_button_copied)
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.new_button)) },
                navigationIcon = {
                    IconButton(onClick = { navigation.onNavigationEvent(NavigationEvent.PopBackStack) }) {
                        Icon(
                            painterResource(Res.drawable.baseline_chevron_left_24),
                            stringResource(Res.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        enabled = state.linkState is LinkState.Ready,
                        onClick = { state.linkState.link?.let { eventHandler.onEvent(ShareButtonEvent.OnShareClick(it)) } }
                    ) {
                        Icon(painterResource(Res.drawable.ic_share_24), shareTitle)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            RoundedButton(
                text = button.actionText,
                containerColor = button.color
            )
            Text(
                stringResource(Res.string.share_button_success),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            when (val linkState = state.linkState) {
                LinkState.Loading -> {
                    CircularProgressIndicator()
                    Text(stringResource(Res.string.share_button_loading))
                }

                LinkState.Failed -> {
                    Text(
                        stringResource(Res.string.share_button_error),
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedButton(onClick = { eventHandler.onEvent(ShareButtonEvent.Retry) }) { Text(stringResource(Res.string.retry)) }
                }

                is LinkState.Ready -> {
                    val qr = state.qr
                    val link = linkState.link

                    if (qr != null) {
                        ShareQrCode(qr)
                    }
                    val linkTitle = stringResource(Res.string.share_button_link)
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClickLabel = linkTitle, role = Role.Button) {
                                eventHandler.onEvent(ShareButtonEvent.OnCopyClick(link))
                            },
                        value = link,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(linkTitle) },
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            eventHandler.onEvent(ShareButtonEvent.OnCopyClick(link))
                        }
                    ) { Text(stringResource(Res.string.share_button_copy)) }
                }
            }
        }
    }

    LaunchedEffect(state.effect) {
        when (state.effect) {
            ShareButtonEffect.OnCopyLink -> {
                snackbarHostState.showSnackbar(shareButtonCopied)
            }

            null -> {}
        }
    }
}

@Composable
private fun ShareQrCode(qr: ImageBitmap) {
    Image(
        bitmap = qr,
        contentDescription = stringResource(Res.string.share_button_qr),
        modifier = Modifier.size(256.dp),
        filterQuality = FilterQuality.None
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ShareButtonPreview() {
    CustomeButtonTheme {
        ShareButtonScreen(
            ShareButtonViewState(
                button = mrpapermoon.customebutton.entity.SharedButton(
                    "preview", Color(0xFFF4A7C1),
                    stringResource(Res.string.example_title),
                    stringResource(Res.string.example_action),
                    stringResource(Res.string.example_notification)
                ),
                linkState = LinkState.Ready("customebutton://join/123e4567-e89b-42d3-a456-426614174000")
            ),
            eventHandler = EventHandler
        )
    }
}
