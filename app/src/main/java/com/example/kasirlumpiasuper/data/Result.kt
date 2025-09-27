package com.example.kasirlumpiasuper.data

import com.example.kasirlumpiasuper.domain.DomainError

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: DomainError) : Result<Nothing>()
}