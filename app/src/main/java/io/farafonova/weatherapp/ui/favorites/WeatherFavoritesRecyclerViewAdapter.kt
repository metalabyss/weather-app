package io.farafonova.weatherapp.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.LocationListItemBinding

class WeatherFavoritesRecyclerViewAdapter(private val onFavoriteClickListener: (String, String) -> Unit) :
    ListAdapter<FavoritesWeatherEntry, WeatherFavoritesViewHolder>(FavoritesWeatherEntryComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherFavoritesViewHolder {
        val binding =
            LocationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherFavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherFavoritesViewHolder, position: Int) {
        val weatherEntry = getItem(position)
        holder.bind(weatherEntry, onFavoriteClickListener)
    }
}

class WeatherFavoritesViewHolder(private val binding: LocationListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: FavoritesWeatherEntry, onClickListener: (String, String) -> Unit) {
        binding.weatherEntry = item
        binding.root.setOnClickListener { onClickListener(item.latitude, item.longitude) }
        binding.executePendingBindings()
    }
}

class FavoritesWeatherEntryComparator : DiffUtil.ItemCallback<FavoritesWeatherEntry>() {
    override fun areItemsTheSame(
        oldItem: FavoritesWeatherEntry,
        newItem: FavoritesWeatherEntry
    ): Boolean {
        return (oldItem.latitude == newItem.latitude) && (oldItem.longitude == newItem.longitude)
    }

    override fun areContentsTheSame(
        oldItem: FavoritesWeatherEntry,
        newItem: FavoritesWeatherEntry
    ): Boolean {
        return oldItem == newItem
    }
}