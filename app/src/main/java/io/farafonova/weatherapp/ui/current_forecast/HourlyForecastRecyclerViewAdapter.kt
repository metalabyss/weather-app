package io.farafonova.weatherapp.ui.current_forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.ListItemHourlyForecastBinding
import io.farafonova.weatherapp.domain.model.HourlyForecast

class HourlyForecastRecyclerViewAdapter :
    ListAdapter<HourlyForecast, HourlyForecastViewHolder>(HourlyForecastComparator()) {
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
        holder.bind(item)
    }
}

class HourlyForecastViewHolder(private val binding: ListItemHourlyForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: HourlyForecast) {
        binding.hourlyForecast = item
        binding.executePendingBindings()
    }
}

class HourlyForecastComparator : DiffUtil.ItemCallback<HourlyForecast>() {
    override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return areContentsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem == newItem
    }
}