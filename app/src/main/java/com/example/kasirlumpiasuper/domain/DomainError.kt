package com.example.kasirlumpiasuper.domain

sealed class DomainError {
    object NetworkError : DomainError()
    object PermissionDenied : DomainError()
    object NotFound : DomainError()
    object PreconditionFailed : DomainError()
    object RateLimited : DomainError()
    data class InvalidInput(val reason: String) : DomainError()
    object UnknownError : DomainError()

    // Printer Related
    object PrinterNotPaired : DomainError()
    object BluetoothPermissionRequired : DomainError()
    object PrinterOutOfPaper : DomainError()
    object PrinterConnectionFailed : DomainError()
}