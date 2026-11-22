package mrpapermoon.customebutton.ui.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mrpapermoon.customebutton.ButtonUiState
import mrpapermoon.customebutton.resources.Res
import mrpapermoon.customebutton.resources.creator_confirm
import mrpapermoon.customebutton.resources.creator_next
import mrpapermoon.customebutton.ui.EventHandler
import mrpapermoon.customebutton.ui.theme.CreatorActionContent
import mrpapermoon.customebutton.ui.theme.CreatorConfirmGreen
import mrpapermoon.customebutton.ui.theme.CreatorNextBlue
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateButtonActionButton(
    viewState: State<CreateButtonViewState>,
    draftState: State<ButtonUiState>,
    eventHandler: EventHandler,
) {
    val viewState = viewState.value
    val isReviewStep = viewState.step == CreateButtonStep.Confirm

    val containerColor = when {
        !isReviewStep -> CreatorNextBlue
        draftState.value.dataIsValid -> CreatorConfirmGreen
        else -> Color.Gray
    }
    Button(
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 24.dp
            )
            .fillMaxWidth()
            .height(52.dp),
        enabled = viewState.enabled && (viewState.canGoNext || isReviewStep),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = CreatorActionContent
        ),
        onClick = {
            eventHandler.onEvent(if (isReviewStep) CreateButtonEvent.Save else CreateButtonEvent.Next)
        }
    ) {
        if (viewState.enabled) {
            val stringResource = if (isReviewStep) Res.string.creator_confirm else Res.string.creator_next
            Text(stringResource(stringResource))
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
        }
    }
}
