package com.example.kasirlumpiasuper.ui.utils

import android.util.Log
import com.example.kasirlumpiasuper.data.model.Order
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptFormatter {

    fun format(order: Order): String {
        val sb = StringBuilder()

        // Header
        sb.append("[C]<b>Lumpia Super</b>\n")
        sb.append("[C]Jl. Ahmad Yani I No. B10\n")
        sb.append("[C](Ruko di belakang Lab Prodia)\n\n")

        // Date & Queue
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("id", "ID"))
            .format(Date(order.createdAt))
        sb.append("[C]$dateStr\n")
        sb.append("[C]Nomor Antrian\n")
        sb.append("[C]<b>${String.format(Locale.getDefault(), "%03d", order.queueNumber)}</b>\n\n")

        // Notes (optional)
        if (order.notes.isNotBlank()) {
            sb.append("Catatan   : ${order.notes}\n")
        }

        // Group items by cupIndex
        val itemsByCup = order.items.groupBy { it.cupIndex }
        itemsByCup.forEach { (cupIndex, items) ->
            sb.append("--------------------------------\n")
            sb.append("CUP $cupIndex\n")
            sb.append("--------------------------------\n")
            items.forEach { item ->
                val name = item.name
                // Format harga satuan dan total
                val effectivePrice = if (item.isFree) 0 else item.unitPrice
                val qtyPrice = "${item.qty}x ${"%,d".format(effectivePrice)}".padEnd(10, ' ')
                val total = "%,d".format(effectivePrice * item.qty).padStart(8, ' ')


                sb.append("$name\n")

                sb.append("$qtyPrice$total\n")

            }
        }

        sb.append("--------------------------------\n")
        sb.append(rightAlignLabelValue("Sub Total", "%,d".format(order.subtotal)) + "\n")
        sb.append(rightAlignLabelValue("Hemat", "%,d".format(order.discount ?: 0)) + "\n")
        sb.append("--------------------------------\n")
        sb.append(rightAlignLabelValue("Total", "%,d".format(order.total)) + "\n")

        if (order.paymentMethod == PaymentMethod.CASH) {
            sb.append(rightAlignLabelValue("Payment", "%,d".format(order.cashReceived ?: 0)) + "\n")
            sb.append(rightAlignLabelValue("Change", "%,d".format(order.change ?: 0)) + "\n")
        } else {
            sb.append(rightAlignLabelValue("Payment", "%,d".format(order.nonCashAmount ?: 0)) + "\n")
        }

        sb.append(rightAlignLabelValue("Metode", order.paymentMethod.toString()) + "\n\n\n")


        // Footer
        sb.append("[C]Terima Kasih\n")
        sb.append("[C]Silahkan Datang Kembali\n")
        sb.append("[C]Instagram: @LUMPIA_SUPER\n")
        sb.append("[C]Terima Pesanan Nasi Kotak\n")
        sb.append("[C]Contact Person: 081-2580-3787\n")

        return sb.toString()
    }
}

fun rightAlign(label: String, value: String, width: Int = 31): String {
    val line = "$label $value"
    return if (line.length >= width) line else line.padStart(width, ' ')
}

fun rightAlignLabelValue(label: String, value: String, width: Int = 32): String {
    val labelWidth = 19 // blok kiri (label + " : ")
    val safeLabel = label.take(labelWidth - 3) // antisipasi label panjang
    val left = safeLabel.padStart(labelWidth - 3) + " : "
    val right = value.padStart(width - labelWidth)
    return left + right
}
