package mrpapermoon.customebutton.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mrpapermoon.customebutton.R
import mrpapermoon.customebutton.ui.theme.ButtonContent
import mrpapermoon.customebutton.ui.theme.ButtonPink

@Composable
fun RoundedButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = ButtonContent,
    shape: Shape = CircleShape,
    containerColor: Color = ButtonPink
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
            color = color,
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
