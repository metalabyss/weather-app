package io.farafonova.weatherapp.ui

import android.widget.CompoundButton
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.TimeZone

@BindingAdapter("compoundButtonOnClick")
fun toggleButtonOnClick(button: CompoundButton, clickListener: CompoundButton.OnCheckedChangeListener) {
    button.setOnCheckedChangeListener(clickListener)
}

@BindingAdapter(value = ["dateAndTime", "pattern", "resourceStringWithArguments"], requireAll = false)
fun setFormattedDateAndTimeAsText(
    textView: TextView,
    dateAndTime: Long,
    pattern: String = "",
    @StringRes stringResId: Int = 0
) {
    val forecastTime = Instant.ofEpochSecond(dateAndTime)
        .atZone(TimeZone.getDefault().toZoneId())

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
