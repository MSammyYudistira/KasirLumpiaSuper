package com.example.kasirlumpiasuper.domain.error

sealed class DomainError {
    object NetworkError : DomainError()
    object PermissionDenied : DomainError()
    object NotFound : DomainError()
    object PreconditionFailed : DomainError()
    object RateLimited : DomainError()
    data class InvalidInput(val reason: String) : DomainError()
    object UnknownError : DomainError()
}