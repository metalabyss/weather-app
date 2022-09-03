package io.farafonova.weatherapp.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.ListItemFavoriteLocationBinding
import io.farafonova.weatherapp.domain.model.BriefCurrentForecastWithLocation
import io.farafonova.weatherapp.domain.model.Location

class WeatherFavoritesRecyclerViewAdapter(private val onFavoriteClickListener: (Double, Double) -> Unit) :
    ListAdapter<BriefCurrentForecastWithLocation, WeatherFavoritesViewHolder>(
        FavoritesWeatherEntryComparator()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherFavoritesViewHolder {
        val binding =
            ListItemFavoriteLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WeatherFavoritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherFavoritesViewHolder, position: Int) {
        val weatherEntry = getItem(position)
        holder.bind(weatherEntry, onFavoriteClickListener)
    }
}

class WeatherFavoritesViewHolder(private val binding: ListItemFavoriteLocationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: BriefCurrentForecastWithLocation, onClickListener: (Double, Double) -> Unit) {
        binding.forecast = item
        binding.root.setOnClickListener {
            onClickListener(
                item.location.latitude,
                item.location.longitude
            )
        }
        binding.executePendingBindings()
    }

    fun getLocation(): Location? = binding.forecast?.location
}

class FavoritesWeatherEntryComparator : DiffUtil.ItemCallback<BriefCurrentForecastWithLocation>() {
    override fun areItemsTheSame(
        oldItem: BriefCurrentForecastWithLocation,
        newItem: BriefCurrentForecastWithLocation
    ): Boolean {
        return (oldItem.location.latitude == newItem.location.latitude)
                && (oldItem.location.latitude == newItem.location.latitude)
    }

    override fun areContentsTheSame(
        oldItem: BriefCurrentForecastWithLocation,
        newItem: BriefCurrentForecastWithLocation
    ): Boolean {
        return oldItem == newItem
    }
}