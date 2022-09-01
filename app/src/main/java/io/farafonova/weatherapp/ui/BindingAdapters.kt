package io.farafonova.weatherapp.ui

import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@BindingAdapter("compoundButtonOnClick")
fun toggleButtonOnClick(button: CompoundButton, clickListener: CompoundButton.OnCheckedChangeListener) {
    button.setOnCheckedChangeListener(clickListener)
}

@BindingAdapter(
    value = ["dateAndTime", "timezoneOffset", "pattern", "resourceStringWithArguments"],
    requireAll = false
)
fun setFormattedDateAndTimeAsText(
    textView: TextView,
    dateAndTime: Long,
    timezoneOffset: Int = ZoneOffset.UTC.totalSeconds,
    pattern: String = "",
    @StringRes stringResId: Int = 0
) {
    val forecastTime = Instant.ofEpochSecond(dateAndTime)
        .atOffset(ZoneOffset.ofTotalSeconds(timezoneOffset))

    val dateTimeString = if (pattern.isBlank()) {
        forecastTime.toString()
    } else {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        forecastTime.format(formatter)
    }

    textView.text = if (stringResId == 0) {
        dateTimeString
    } else {
        textView.context.getString(stringResId, dateTimeString)
    }
}
