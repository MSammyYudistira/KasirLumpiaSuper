package com.example.kasirlumpiasuper.domain.error

import com.google.firebase.firestore.FirebaseFirestoreException

object ErrorMapper {

    fun mapFirestoreException(e: FirebaseFirestoreException): DomainError {
        return when (e.code) {
            FirebaseFirestoreException.Code.UNAVAILABLE,
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> DomainError.NetworkError
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> DomainError.PermissionDenied
            FirebaseFirestoreException.Code.FAILED_PRECONDITION -> DomainError.PreconditionFailed
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> DomainError.RateLimited
            else -> DomainError.UnknownError
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