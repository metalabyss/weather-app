package io.farafonova.weatherapp.ui.search

import android.widget.CompoundButton
import androidx.databinding.BindingAdapter

@BindingAdapter("compoundButtonOnClick")
fun toggleButtonOnClick(button: CompoundButton, clickListener: CompoundButton.OnCheckedChangeListener) {
    button.setOnCheckedChangeListener(clickListener)
}
