package io.farafonova.weatherapp.ui

data class SnackbarOptions(
    val notificationText: UiText,
    val actionText: UiText,
    val action: () -> Unit
)
