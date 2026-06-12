package com.netinventory.app.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object QrUtils {

    /** Generează un Bitmap cu codul QR pentru [content]. Totul se face local. */
    fun generate(content: String, sizePx: Int = 512): Bitmap {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 1
        )
        val matrix = QRCodeWriter().encode(
            content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints
        )
        val w = matrix.width
        val h = matrix.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (matrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, w, 0, 0, w, h)
        }
    }

    fun generateImageBitmap(content: String, sizePx: Int = 512): ImageBitmap =
        generate(content, sizePx).asImageBitmap()
}

object DateUtils {
    private val full = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("ro"))
    private val short = SimpleDateFormat("dd MMM yyyy", Locale("ro"))

    fun formatFull(ts: Long): String = full.format(Date(ts))
    fun formatShort(ts: Long?): String = if (ts == null) "—" else short.format(Date(ts))
}
