package com.katyrin.testmapbox.viewmodel

sealed class AppState<out T> {
    data class Success<out T>(val value: T) : AppState<T>()
    data class Error(val message: String?) : AppState<Nothing>()
    object Loading : AppState<Nothing>()
}