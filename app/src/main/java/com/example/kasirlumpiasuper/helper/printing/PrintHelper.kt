package com.example.kasirlumpiasuper.helper.printing

import android.content.Context
import android.widget.Toast
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.example.kasirlumpiasuper.domain.model.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintHelper {

    private var printerConnection: DeviceConnection? = null

    fun initPrinter(context: Context): Boolean {
        return try {
            printerConnection = BluetoothPrintersConnections.selectFirstPaired()

            if (printerConnection == null ) {
                Toast.makeText(context, "Printer belum tersambung", Toast.LENGTH_SHORT).show()
                false
            } else {
                Toast.makeText(context, "Printer tersambung", Toast.LENGTH_SHORT).show()
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal inisialisasi printer: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }

    suspend fun printReceipt(context: Context, order: Order) = withContext(Dispatchers.IO) {
        try {

            if (printerConnection == null) {
                printerConnection = BluetoothPrintersConnections.selectFirstPaired()
            }

            if (printerConnection == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Printer tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
                return@withContext
            }

            val printer = EscPosPrinter(printerConnection, 203, 40f, 32)
            val receiptText = ReceiptFormatter.format(order)

            printer.printFormattedText(receiptText)

            Thread.sleep(1000L)

            printer.disconnectPrinter()
            printerConnection = null

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Struk berhasil dicetak", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak: ${e.message}", Toast.LENGTH_LONG).show()
            }
            printerConnection = null
        }
    }

    suspend fun printQueueNumber(context: Context, queueNumber: Int) = withContext(Dispatchers.IO) {
        try {
            if (printerConnection == null) {
                printerConnection = BluetoothPrintersConnections.selectFirstPaired()
            }

            if (printerConnection == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Printer tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
                return@withContext
            }

            val printer = EscPosPrinter(printerConnection, 203, 40f, 32)
            val queueText = buildString {
                append("[C]<b>Lumpia Super</b>\n")
                append("[C]Jl. Ahmad Yani I No. B10\n")
                append("[C](Ruko di belakang Lab Prodia)\n\n")

                val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("id", "ID"))
                    .format(Date())
                append("[C]$dateStr\n")
                append("[C]Nomor Antrian\n")
                append("[C]<font size='big'><b>${String.format(Locale.getDefault(), "%03d", queueNumber)}</b></font>\n\n")
            }

            printer.printFormattedText(queueText)
            Thread.sleep(1000L)
            printer.disconnectPrinter()
            printerConnection = null

            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Nomor antrian #$queueNumber berhasil dicetak", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Gagal mencetak antrian: ${e.message}", Toast.LENGTH_LONG).show()
            }
            printerConnection = null
        }
    }
}