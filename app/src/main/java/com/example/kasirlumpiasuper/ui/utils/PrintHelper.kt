package com.example.kasirlumpiasuper.ui.utils

import android.content.Context
import android.util.Printer
import android.widget.Toast
import com.example.kasirlumpiasuper.data.model.Order
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.example.kasirlumpiasuper.domain.DomainError

object PrintHelper {

    fun printReceipt(context: Context, order: Order) {
        try {
            val printerConnection = BluetoothPrintersConnections.selectFirstPaired()
            if(printerConnection == null) {
                Toast.makeText(context, "Printer tidak ditemukan", Toast.LENGTH_SHORT).show()
                return
            }

            val printer = EscPosPrinter(printerConnection, 203, 40f, 32)
            val receiptText = ReceiptFormatter.format(order)

            printer.printFormattedText(receiptText)

            Toast.makeText(context, "Struk berhasil dicetak", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal mencetal: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun mapPrinterException(e: Exception): DomainError {
        return when {
            e.message?.contains("Out of paper", ignoreCase = true) == true -> DomainError.PrinterOutOfPaper
            e.message?.contains("not paired", ignoreCase = true) == true -> DomainError.PrinterNotPaired
            else -> DomainError.PrinterConnectionFailed
        }
    }
}