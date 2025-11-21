package com.example.kasirlumpiasuper.ui.recap

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.kasirlumpiasuper.R
import com.example.kasirlumpiasuper.domain.model.CashAtRegister
import com.example.kasirlumpiasuper.domain.model.DailyRecap
import com.example.kasirlumpiasuper.domain.model.ExpenseSummary
import com.example.kasirlumpiasuper.domain.model.FreeSummary
import com.example.kasirlumpiasuper.domain.model.GrossSection
import com.example.kasirlumpiasuper.domain.model.ProductRecapRow
import com.example.kasirlumpiasuper.ui.components.CustomTopBarWithBackAction
import com.example.kasirlumpiasuper.ui.theme.Danger
import com.example.kasirlumpiasuper.ui.theme.Primary
import com.example.kasirlumpiasuper.ui.theme.Secondary
import com.example.kasirlumpiasuper.ui.theme.Success
import com.example.kasirlumpiasuper.ui.theme.Surface
import com.example.kasirlumpiasuper.ui.theme.Warning
import com.example.kasirlumpiasuper.helper.date.DateUtils
import com.example.kasirlumpiasuper.helper.printing.PdfUtils
import java.io.File

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DetailRecapScreen(
    navController: NavHostController,
    recapViewModel: RecapViewModel,
    dateLabel: String
) {
    val isLoading by recapViewModel.isLoading.collectAsState()
    val recap by recapViewModel.recap.collectAsState()
    val error by recapViewModel.error.collectAsState()

    var showPdfDialog by remember { mutableStateOf(false) }
    var generatedPdfUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    LaunchedEffect(dateLabel) {
        recapViewModel.load(dateLabel)
    }

    Scaffold(
        topBar = {
            CustomTopBarWithBackAction(
                onBackClick = { navController.popBackStack() },
                title = "Detail Rekapan"
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            error != null -> Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(error ?: "Error", color = Color.Red)
            }

            recap == null -> Box(
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text("Belum ada data untuk $dateLabel")
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 72.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    item {
                        HeaderInfo(recap!!)
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "Rekapan Jumlah Makanan",
                                    style = MaterialTheme.typography.displaySmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                RecapTableDynamic(rows = recap?.productRows ?: emptyList())

                            }
                        }
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                RecapCardFree(
                                    "Barang Gratis (Free)",
                                    recap!!.freeSummary,
                                    Modifier.weight(1f)
                                )
                                RecapCardGross(
                                    "Pendapatan",
                                    recap!!.grossSection,
                                    Modifier.weight(1f)
                                )
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                RecapCardExpense(
                                    "Pengeluaran Hari Ini",
                                    recap!!.expenseSummary,
                                    Modifier.weight(1f)
                                )
                                RecapCardCash(
                                    "Uang Tunai di Kasir",
                                    recap!!.cashAtRegister,
                                    Modifier.weight(1f)
                                )
                            }

                            if (recap!!.notes.isNotEmpty()) {
                                RecapCardNotes(
                                    title = "Catatan",
                                    notes = recap!!.notes
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                val widthPx = PdfUtils.screenWidthPx(context)
                                val bitmap = PdfUtils.renderComposableToBitmap(
                                    context = context,
                                    widthPx = widthPx,
                                    content = {
                                        DetailRecapBodyExport(
                                            recap = recap!!,
                                            paddingHorizontal = 72.dp
                                        )
                                    },
                                    highQuality = true
                                )

                                val fileName = PdfUtils.defaultRecapFileName(recap!!.dateLabel)
                                val ok = PdfUtils.saveBitmapAsPdfToDownloads(
                                    context = context,
                                    bitmap = bitmap,
                                    fileName = fileName,
                                    dpi = 300
                                )

                                if (ok) {
                                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    val candidateNames = listOf(
                                        "$fileName.pdf",
                                        fileName,
                                    )
                                    var pdfFile: File? = null
                                    for (name in candidateNames) {
                                        val tryFile = File(downloadsDir, name)
                                        if (tryFile.exists()) {
                                            pdfFile = tryFile
                                            break
                                        }
                                    }

                                    if (pdfFile == null) {
                                        val internalFile = File(context.getExternalFilesDir(null), "$fileName.pdf")
                                        if (internalFile.exists()) pdfFile = internalFile
                                    }

                                    if (pdfFile != null && pdfFile.exists()) {
                                        val uri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.provider",
                                            pdfFile
                                        )
                                        generatedPdfUri = uri
                                        showPdfDialog = true
                                    } else {
                                        Toast.makeText(context, "PDF tersimpan, tapi file tidak ditemukan.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Gagal menyimpan PDF", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary)
                        ) {
                            Text(
                                "Cetak Rekapan",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        if (showPdfDialog && generatedPdfUri != null) {
                            AlertDialog(
                                onDismissRequest = { showPdfDialog = false },
                                containerColor = Color.White,
                                title = { Text("Rekapan Disimpan", style = MaterialTheme.typography.displaySmall) },
                                text = { Text("Rekapan telah disimpan dalam bentuk PDF di folder Download. Apakah kamu ingin membukanya sekarang?", style = MaterialTheme.typography.bodyMedium) },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showPdfDialog = false
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(generatedPdfUri, "application/pdf")
                                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        try {
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Tidak ada aplikasi untuk membuka PDF", Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Text("Ya")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showPdfDialog = false }) {
                                        Text("Tidak")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderInfo(
    recap: DailyRecap,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.baseline_date_range_24),
            contentDescription = "Tanggal"
        )
        Text(recap.dateLabel, style = MaterialTheme.typography.titleMedium)

        if (recap.location.isNotBlank()) {
            Spacer(Modifier.width(16.dp))
            Icon(
                painter = painterResource(R.drawable.baseline_location_pin_24),
                contentDescription = null
            )
            Text(recap.location, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.width(16.dp))

        if (recap.userName.isNotBlank()) {
            Icon(
                painter = painterResource(R.drawable.baseline_person_24),
                contentDescription = "Nama User"
            )
            Text(recap.userName, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun DetailRecapBodyExport(
    recap: DailyRecap,
    paddingHorizontal: Dp = 72.dp,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingHorizontal, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        HeaderInfo(recap)
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Black),
            color = Surface,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Rekapan Jumlah Makanan",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                RecapTableDynamic(rows = recap.productRows)
            }
        }

        // Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RecapCardFree(
                    "Barang Gratis (Free)",
                    recap.freeSummary,
                    Modifier
                        .weight(1f)
                        .height(280.dp)
                        .border(width = 1.dp, color = Color.Black)
                )
                RecapCardGross(
                    "Pendapatan",
                    recap.grossSection,
                    Modifier
                        .weight(1f)
                        .height(280.dp)
                        .border(width = 1.dp, color = Color.Black)
                )
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RecapCardExpense(
                    "Pengeluaran Hari Ini",
                    recap.expenseSummary,
                    Modifier
                        .weight(1f)
                        .height(280.dp)
                        .border(width = 1.dp, color = Color.Black)
                )
                RecapCardCash(
                    "Uang Tunai di Kasir",
                    recap.cashAtRegister,
                    Modifier
                        .weight(1f)
                        .height(280.dp)
                        .border(width = 1.dp, color = Color.Black)
                )
            }
            RecapCardNotes(
                title = "Catatan",
                notes = recap.notes,
                Modifier
                    .border(width = 1.dp, color = Color.Black)

            )
        }
    }
}

@Composable
fun RecapTableDynamic(rows: List<ProductRecapRow>) {
    val columnWeights = listOf(2f, 1f, 1f, 1f, 1f, 1.5f)
    val alignments = listOf(
        TextAlign.Start,
        TextAlign.Center,
        TextAlign.Center,
        TextAlign.Center,
        TextAlign.Center,
        TextAlign.End
    )

    val headers =
        listOf("Nama Makanan", "Stok Awal", "Stok Akhir", "Rusak / Retur", "Terjual", "Pendapatan")

    Column(Modifier.fillMaxWidth()) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .background(Secondary)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            headers.forEachIndexed { i, h ->
                TableCell(h, Modifier.weight(columnWeights[i]), bold = true, align = alignments[i])
            }
        }
        // Body
        rows.forEachIndexed { idx, r ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                TableCell(r.name, Modifier.weight(columnWeights[0]), align = alignments[0])
                TableCell(
                    r.initialStock.toString(),
                    Modifier.weight(columnWeights[1]),
                    align = alignments[1]
                )
                TableCell(
                    r.endingStock.toString(),
                    Modifier.weight(columnWeights[2]),
                    align = alignments[2]
                )
                TableCell(
                    r.damagedStock.toString(),
                    Modifier.weight(columnWeights[3]),
                    align = alignments[3]
                )
                TableCell(
                    r.sold.toString(),
                    Modifier.weight(columnWeights[4]),
                    align = alignments[4]
                )
                TableCell(
                    DateUtils.rupiah(r.revenue),
                    Modifier.weight(columnWeights[5]),
                    align = alignments[5]
                )
            }
            if (idx < rows.lastIndex) Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)
        }
        // Footer total
        val totalSold = rows.sumOf { it.sold }
        val totalRevenue = rows.sumOf { it.revenue }
        Row(
            Modifier
                .fillMaxWidth()
                .background(Secondary)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            TableCell("Total", Modifier.weight(columnWeights[0]), bold = true)
            TableCell("", Modifier.weight(columnWeights[1]))
            TableCell("", Modifier.weight(columnWeights[2]))
            TableCell("", Modifier.weight(columnWeights[3]))
            TableCell(
                totalSold.toString(),
                Modifier.weight(columnWeights[4]),
                bold = true,
                align = TextAlign.Center
            )
            TableCell(
                DateUtils.rupiah(totalRevenue),
                Modifier.weight(columnWeights[5]),
                bold = true,
                align = TextAlign.End
            )
        }
    }
}

@Composable
fun RecapCardFree(title: String, data: FreeSummary, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Surface,
        shadowElevation = 2.dp,
        modifier = modifier.height(250.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Nominal Harga", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.totalNominal),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Jumlah Item", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${data.totalItems}",
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun RecapCardExpense(title: String, data: ExpenseSummary, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Surface,
        shadowElevation = 2.dp,
        modifier = modifier.height(250.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Barang Gratis", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.freeNominal),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Diskon", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.discountTotal),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Air Mineral", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.mineralWater),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Pengeluaran Lainnya", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.otherExpense),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Jumlah", style = MaterialTheme.typography.titleLarge)
                Text(
                    DateUtils.rupiah(data.sum),
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary
                )
            }
        }
    }
}


@Composable
fun RecapCardGross(title: String, data: GrossSection, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Surface,
        shadowElevation = 2.dp,
        modifier = modifier.height(250.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Jumlah 1:", style = MaterialTheme.typography.titleMedium)
                Text(
                    DateUtils.rupiah(data.sum1),
                    color = Primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Uang Non-Tunai:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.nonCash),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pengeluaran Hari Ini:", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.expenseToday),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Jumlah 2:", style = MaterialTheme.typography.titleMedium)
                Text(
                    DateUtils.rupiah(data.sum2),
                    style = MaterialTheme.typography.titleMedium,
                    color = Primary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Uang Kas (pembuka):", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.cashOpening),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Laba Bersih:", style = MaterialTheme.typography.titleLarge)
                Text(
                    DateUtils.rupiah(data.sum3),
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary
                )
            }
        }
    }
}

@Composable
fun RecapCardCash(title: String, data: CashAtRegister, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Surface,
        shadowElevation = 2.dp,
        modifier = modifier.height(250.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Uang Besar", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.bigCash),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Uang Kecil", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.smallCash),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Uang Lebihan", style = MaterialTheme.typography.bodyMedium)
                Text(
                    DateUtils.rupiah(data.extraCash),
                    color = Primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Jumlah", style = MaterialTheme.typography.titleLarge)
                Text(
                    DateUtils.rupiah(data.sum),
                    style = MaterialTheme.typography.titleLarge,
                    color = Primary
                )
            }

            val diff = data.diff
            val diffColor = when {
                diff >= 100_000 -> Danger
                diff == 0 -> Success
                else -> Warning
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Selisih", style = MaterialTheme.typography.titleLarge)
                Text(
                    DateUtils.rupiah(data.diff),
                    color = diffColor,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun RecapCardNotes(title: String, notes: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Surface,
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(notes, style = MaterialTheme.typography.bodyMedium)

        }
    }
}

@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
    align: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = if (bold) MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        else MaterialTheme.typography.bodySmall,
        modifier = modifier.padding(horizontal = 4.dp),
        textAlign = align
    )
}

