package io.farafonova.weatherapp.ui.current_forecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.ListItemDailyForecastBinding
import io.farafonova.weatherapp.domain.model.BriefDailyForecastWithLocation

class DailyForecastRecyclerViewAdapter :
    ListAdapter<BriefDailyForecastWithLocation, BriefDailyForecastViewHolder>(BriefDailyForecastComparator()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BriefDailyForecastViewHolder {

        val binding = ListItemDailyForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BriefDailyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BriefDailyForecastViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class BriefDailyForecastViewHolder(private val binding: ListItemDailyForecastBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: BriefDailyForecastWithLocation) {
        binding.forecast = item
        binding.executePendingBindings()
    }
}

class BriefDailyForecastComparator : DiffUtil.ItemCallback<BriefDailyForecastWithLocation>() {
    override fun areItemsTheSame(
        oldItem: BriefDailyForecastWithLocation,
        newItem: BriefDailyForecastWithLocation
    ): Boolean {
        return areContentsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(
        oldItem: BriefDailyForecastWithLocation,
        newItem: BriefDailyForecastWithLocation
    ): Boolean {
        return oldItem == newItem
    }
}