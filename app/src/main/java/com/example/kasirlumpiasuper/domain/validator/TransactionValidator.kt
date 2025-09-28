package com.example.kasirlumpiasuper.domain.validator

import com.example.kasirlumpiasuper.data.model.OrderItem
import com.example.kasirlumpiasuper.data.model.PaymentMethod
import com.example.kasirlumpiasuper.domain.error.DomainError

object TransactionValidator {

    fun validateTransaction(
        items: Map<Int, List<OrderItem>>,
        total: Int,
        queueNumber: Int?,
        paymentMethod: PaymentMethod,
        cashReceived: Int,
        nonCashAmount: Int
    ): DomainError? {
        if (items.values.flatten().isEmpty()) return DomainError.InvalidInput("Pesanan kosong")
        if (total < 0) return DomainError.InvalidInput("Total tidak valid")
        if (queueNumber == null) return DomainError.InvalidInput("Nomor antrian belum siap")

        return when (paymentMethod) {
            PaymentMethod.CASH -> if (cashReceived < total)
                DomainError.InvalidInput("Uang tidak mencukupi")
            else null

            PaymentMethod.CASHLESS -> if (nonCashAmount != total)
                DomainError.InvalidInput("Nominal non-tunai tidak sesuai total")
            else null
        }
    }
}