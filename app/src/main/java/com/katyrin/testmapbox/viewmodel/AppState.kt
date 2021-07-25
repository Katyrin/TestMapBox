package com.katyrin.testmapbox.viewmodel

sealed class AppState<out T> {
    data class Success<out T>(val value: T) : AppState<T>()
    data class ClientError(val code: Int) : AppState<Nothing>()
    object ServerError : AppState<Nothing>()
    object Loading : AppState<Nothing>()
}