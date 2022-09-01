package io.farafonova.weatherapp.ui.current_forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.ListItemHourlyForecastBinding
import io.farafonova.weatherapp.domain.model.HourlyForecastWithLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class HourlyForecastRecyclerViewAdapter(private val isItLight: suspend (Long) -> Boolean) :
    ListAdapter<HourlyForecastWithLocation, HourlyForecastViewHolder>(HourlyForecastComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {

        val binding = ListItemHourlyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HourlyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val item = getItem(position)
        val isItLight = runBlocking(Dispatchers.IO) { isItLight(item.forecastTime) }

        val icon = if (isItLight) {
            item.weatherCondition.daySmallIcon()
        } else {
            item.weatherCondition.nightSmallIcon()
        }
        holder.bind(item, icon)
    }
}

class HourlyForecastViewHolder(private val binding: ListItemHourlyForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HourlyForecastWithLocation, @DrawableRes iconResId: Int) {
        binding.forecast = item
        binding.iconResId = iconResId
        binding.executePendingBindings()
    }
}

class HourlyForecastComparator : DiffUtil.ItemCallback<HourlyForecastWithLocation>() {
    override fun areItemsTheSame(oldItem: HourlyForecastWithLocation, newItem: HourlyForecastWithLocation): Boolean {
        return areContentsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: HourlyForecastWithLocation, newItem: HourlyForecastWithLocation): Boolean {
        return oldItem == newItem
    }
}