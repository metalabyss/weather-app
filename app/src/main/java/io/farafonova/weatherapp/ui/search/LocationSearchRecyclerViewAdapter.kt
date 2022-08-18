package io.farafonova.weatherapp.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.ListItemLocationSearchBinding
import io.farafonova.weatherapp.domain.model.Location
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel

class LocationSearchRecyclerViewAdapter(private val parentViewModel: WeatherApplicationViewModel) :
    ListAdapter<Location, LocationSearchViewHolder>(LocationSearchEntryComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationSearchViewHolder {
        val binding = ListItemLocationSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationSearchViewHolder, position: Int) {
        val searchEntry = getItem(position)
        holder.bind(searchEntry, parentViewModel)
    }
}


class LocationSearchViewHolder(private val binding: ListItemLocationSearchBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Location, parentViewModel: WeatherApplicationViewModel) {
            binding.searchEntry = item
            binding.parentViewModel = parentViewModel
            binding.executePendingBindings()
        }
    }


class LocationSearchEntryComparator : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(
        oldItem: Location,
        newItem: Location
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Location,
        newItem: Location
    ): Boolean {
        return oldItem == newItem
    }
}