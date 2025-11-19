package com.example.kasirlumpiasuper.ui.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale


object PdfUtils {

    /**
     * Render composable ke Bitmap dengan tinggi tak terbatas (UNSPECIFIED),
     * sehingga seluruh konten ikut ter-render (penting untuk export tanpa LazyColumn).
     * widthPx sebaiknya pakai lebar layar agar style/spacing tetap konsisten.
     */

    fun renderComposableToBitmap(
        context: Context,
        widthPx: Int,
        content: @Composable () -> Unit,
        highQuality: Boolean = true
    ): Bitmap {
        val activity = context as? Activity
            ?: throw IllegalStateException("Context harus berasal dari Activity")

        // Buat ComposeView dan isi kontennya
        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent { content() }
        }

        // Attach sementara ke window host
        val rootView = activity.window.decorView as ViewGroup
        rootView.addView(composeView)

        // Ukur layout penuh
        composeView.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

        // Render ke bitmap normal (tanpa scaling)
        val baseBitmap = createBitmap(
            composeView.measuredWidth.coerceAtLeast(1),
            composeView.measuredHeight.coerceAtLeast(1)
        )
        val canvas = Canvas(baseBitmap)
        composeView.draw(canvas)

        // Lepas dari root view
        rootView.removeView(composeView)

        // Jika highQuality aktif → skala hasilnya (misal 2x)
        return if (highQuality) {
            val scaledWidth = baseBitmap.width * 2
            val scaledHeight = baseBitmap.height * 2
            Bitmap.createScaledBitmap(baseBitmap, scaledWidth, scaledHeight, true)
        } else {
            baseBitmap
        }
    }




    /**
     * Simpan Bitmap sebagai PDF A4 (595x842 pt). Bitmap akan diskalakan proporsional.
     * File langsung disimpan ke folder Download:
     *  - Android 10+ (Q): via MediaStore (tanpa permission).
     *  - < Android 10: ke Environment.DIRECTORY_DOWNLOADS (perlu WRITE_EXTERNAL_STORAGE).
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBitmapAsPdfToDownloads(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        dpi: Int = 300 // ✅ tambahkan density target (semakin besar = semakin tajam)
    ): Boolean {
        return try {
            val pdfDocument = PdfDocument()

            // Ukuran A4 dalam points (1 point = 1/72 inch)
            val pageWidthPt = 595
            val pageHeightPt = 842

            // Ubah ke pixel berdasarkan DPI target
            val inchToPx = dpi / 72f
            val pageWidthPx = (pageWidthPt * inchToPx).toInt()
            val pageHeightPx = (pageHeightPt * inchToPx).toInt()

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidthPx, pageHeightPx, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Hitung skala agar gambar tidak pecah & pas secara proporsional
            val scale = minOf(
                pageWidthPx.toFloat() / bitmap.width,
                pageHeightPx.toFloat() / bitmap.height
            )

            val scaledWidth = (bitmap.width * scale).toInt()
            val scaledHeight = (bitmap.height * scale).toInt()

            // 🔹 Posisi: di tengah atas (center secara horizontal, top = 0)
            val left = (pageWidthPx - scaledWidth) / 2f
            val top = 0f

            // Gambar latar putih (biar gak abu2)
            canvas.drawColor(Color.WHITE)

            // Gambar bitmap tajam tanpa anti-alias berlebih
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            paint.isFilterBitmap = false // penting biar gak blur

            val scaledBitmap = bitmap.scale(scaledWidth, scaledHeight, false)
            canvas.drawBitmap(scaledBitmap, left, top, paint)

            pdfDocument.finishPage(page)

            // Simpan ke folder Download
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: return false
            resolver.openOutputStream(uri)?.use { pdfDocument.writeTo(it) }
            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)

            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun defaultRecapFileName(dateLabel: String): String {
        // dateLabel kamu sudah format Indonesia (mis. "07 Oktober 2025")
        val safe = dateLabel.replace(" ", "_")
        return "Rekapan_Lumper_$safe.pdf"
    }

    fun screenWidthPx(context: Context): Int {
        val dm: DisplayMetrics = context.resources.displayMetrics
        return dm.widthPixels
    }
}