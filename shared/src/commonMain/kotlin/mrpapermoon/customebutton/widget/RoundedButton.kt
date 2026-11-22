package mrpapermoon.customebutton.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mrpapermoon.customebutton.ui.theme.contentColor

/**
 * Draws a shaped button preview with an automatically sized single-line label.
 *
 * @param text Label to display.
 * @param modifier Modifier applied to the preview.
 * @param textColor Label color.
 * @param shape Outline of the preview.
 * @param containerColor Background color of the preview.
 */
@Composable
fun RoundedButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = containerColor.contentColor,
    shape: Shape = CircleShape,
    containerColor: Color = Color(0xFFF4A7C1)
) {
    Box(
        modifier = modifier
            .size(144.dp)
            .clip(shape)
            .background(containerColor)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = textColor,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 18.sp,
                maxFontSize = 72.sp
            ),
            fontWeight = FontWeight.Bold,
            softWrap = false,
            maxLines = 1,
        )
    }
}
