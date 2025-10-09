package com.example.kasirlumpiasuper.ui.utils

import android.content.Context
import android.widget.Toast
import com.example.kasirlumpiasuper.data.model.Order
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.DeviceConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}