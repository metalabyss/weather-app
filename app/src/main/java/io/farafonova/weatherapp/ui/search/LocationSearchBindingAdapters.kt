package io.farafonova.weatherapp.ui.search

import android.view.View
import android.widget.CompoundButton
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter

@BindingAdapter("toggleButtonOnClick")
fun toggleButtonOnClick(toggleButton: ToggleButton, clickListener: CompoundButton.OnCheckedChangeListener) {
    toggleButton.setOnCheckedChangeListener(clickListener)
}
