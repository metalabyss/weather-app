package io.farafonova.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.LocationListItemBinding

class WeatherFavoritesRecyclerViewAdapter(private val weatherEntryList: List<FavoritesWeatherEntry>):
    RecyclerView.Adapter<WeatherFavoritesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherFavoritesViewHolder {
        val binding = LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherFavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherFavoritesViewHolder, position: Int) {
        if (position < weatherEntryList.size) {
            val weatherEntry = weatherEntryList[position]
            holder.binding.tvItemTemperature.text = weatherEntry.temperature.toString()
            holder.binding.tvItemLocationName.text = weatherEntry.locationName
        }
    }

    override fun getItemCount(): Int {
        return weatherEntryList.size
    }
}

class WeatherFavoritesViewHolder(val binding: LocationListItemBinding): RecyclerView.ViewHolder(binding.root)