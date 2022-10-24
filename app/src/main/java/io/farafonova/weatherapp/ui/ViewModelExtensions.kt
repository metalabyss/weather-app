package io.farafonova.weatherapp.ui

import kotlinx.coroutines.flow.MutableStateFlow

suspend fun <T> runSuspendFunctionWithProgressIndicator(
    isProgressIndicatorShowing: MutableStateFlow<Boolean>,
    task: suspend () -> T,
    exceptionHandler: suspend (Throwable) -> Unit
): T? {
    isProgressIndicatorShowing.value = true
    val result = try {
        task.invoke()
    } catch (throwable: Throwable) {
        exceptionHandler.invoke(throwable)
        null
    }
    isProgressIndicatorShowing.value = false
    return result
}