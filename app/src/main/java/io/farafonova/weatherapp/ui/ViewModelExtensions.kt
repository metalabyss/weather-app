package io.farafonova.weatherapp.ui

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
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

suspend fun printErrorMessageToLogAndShowItToUser(
    tag: String,
    throwable: Throwable,
    errorMessage: MutableSharedFlow<String>
) {
    throwable.localizedMessage?.let { e -> errorMessage.emit(e) }
    Log.e(
        tag,
        "${throwable::class.simpleName}: ${throwable.message}"
    )
}