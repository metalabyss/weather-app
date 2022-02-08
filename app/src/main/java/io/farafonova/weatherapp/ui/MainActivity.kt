package io.farafonova.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.ui.favorites.WeatherFavoritesFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, WeatherFavoritesFragment())
                .commit()
        }
    }
}