package mrpapermoon.customebutton.qr

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Renders a black and white QR bitmap using ZXing on Android. */
class ZxingQrGenerator : QrGenerator {
    override suspend fun generate(link: String): ImageBitmap = withContext(Dispatchers.Default) {
        val matrix = QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, 768, 768)
        val pixels = IntArray(matrix.width * matrix.height) { index ->
            if (matrix[index % matrix.width, index / matrix.width]) Color.BLACK else Color.WHITE
        }
        Bitmap.createBitmap(pixels, matrix.width, matrix.height, Bitmap.Config.ARGB_8888).asImageBitmap()
    }
}
