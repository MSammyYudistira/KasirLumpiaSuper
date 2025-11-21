package com.example.kasirlumpiasuper.helper.printing

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
    fun renderComposableToBitmap(
        context: Context,
        widthPx: Int,
        content: @Composable () -> Unit,
        highQuality: Boolean = true
    ): Bitmap {
        val activity = context as? Activity
            ?: throw IllegalStateException("Context harus berasal dari Activity")

        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent { content() }
        }

        val rootView = activity.window.decorView as ViewGroup
        rootView.addView(composeView)

        composeView.measure(
            View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

        val baseBitmap = createBitmap(
            composeView.measuredWidth.coerceAtLeast(1),
            composeView.measuredHeight.coerceAtLeast(1)
        )
        val canvas = Canvas(baseBitmap)
        composeView.draw(canvas)
        rootView.removeView(composeView)

        return if (highQuality) {
            val scaledWidth = baseBitmap.width * 2
            val scaledHeight = baseBitmap.height * 2
            Bitmap.createScaledBitmap(baseBitmap, scaledWidth, scaledHeight, true)
        } else {
            baseBitmap
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBitmapAsPdfToDownloads(
        context: Context,
        bitmap: Bitmap,
        fileName: String,
        dpi: Int = 300
    ): Boolean {
        return try {
            val pdfDocument = PdfDocument()
            val pageWidthPt = 595
            val pageHeightPt = 842
            val inchToPx = dpi / 72f
            val pageWidthPx = (pageWidthPt * inchToPx).toInt()
            val pageHeightPx = (pageHeightPt * inchToPx).toInt()

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidthPx, pageHeightPx, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val scale = minOf(
                pageWidthPx.toFloat() / bitmap.width,
                pageHeightPx.toFloat() / bitmap.height
            )

            val scaledWidth = (bitmap.width * scale).toInt()
            val scaledHeight = (bitmap.height * scale).toInt()

            val left = (pageWidthPx - scaledWidth) / 2f
            val top = 0f

            canvas.drawColor(Color.WHITE)

            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            paint.isFilterBitmap = false

            val scaledBitmap = bitmap.scale(scaledWidth, scaledHeight, false)
            canvas.drawBitmap(scaledBitmap, left, top, paint)

            pdfDocument.finishPage(page)

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
        val safe = dateLabel.replace(" ", "_")
        return "Rekapan_Lumper_$safe.pdf"
    }

    fun screenWidthPx(context: Context): Int {
        val dm: DisplayMetrics = context.resources.displayMetrics
        return dm.widthPixels
    }
}