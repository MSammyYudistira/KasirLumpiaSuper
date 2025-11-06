package com.example.kasirlumpiasuper.ui.midtrans

// MidtransWebViewScreen.kt
import android.R.attr.navigationIcon
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction

@Composable
fun MidtransWebViewScreen(
    url: String,
    onClose: () -> Unit,
    onFinishSuccess: () -> Unit,  // dipanggil saat URL finish/success terdeteksi
) {
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                title = "Pembayaran QRIS",
                onBackClick = { onClose() }
            )
        }
    ) { inner ->
        Column(Modifier.padding(inner)) {
            if (isLoading) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress / 100f
                                isLoading = newProgress < 100
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val target = request?.url?.toString() ?: return false
                                return handlePossibleFinish(target, onFinishSuccess)
                                    .also { if (!it) view?.loadUrl(target) }
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                                url?.let { handlePossibleFinish(it, onFinishSuccess) }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                url?.let { handlePossibleFinish(it, onFinishSuccess) }
                            }
                        }
                        loadUrl(url)
                    }
                }
            )
        }
    }

    BackHandler { onClose() }
}

/**
 * Deteksi URL "finish/sukses" dari Midtrans VTWeb.
 * Sandbox biasanya redirect ke URL yang mengandung "status_code=200" atau "transaction_status=settlement" atau path "finish".
 * Return true jika sudah ditangani (agar WebView tidak lanjut load default).
 */
private fun handlePossibleFinish(target: String, onFinishSuccess: () -> Unit): Boolean {
    val uri = runCatching { Uri.parse(target) }.getOrNull() ?: return false
    val statusCode = uri.getQueryParameter("status_code")
    val transStatus = uri.getQueryParameter("transaction_status")
    val isFinishPath = uri.path?.contains("finish", ignoreCase = true) == true

    val success = statusCode == "200" || transStatus?.equals("settlement", true) == true || isFinishPath
    if (success) {
        onFinishSuccess()
        return true
    }
    return false
}
