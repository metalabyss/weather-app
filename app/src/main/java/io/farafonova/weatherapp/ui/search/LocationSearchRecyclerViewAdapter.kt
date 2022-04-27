package io.farafonova.weatherapp.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.LocationSearchResultItemBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel

class LocationSearchRecyclerViewAdapter(private val parentViewModel: WeatherApplicationViewModel) :
    ListAdapter<LocationSearchEntry, LocationSearchViewHolder>(LocationSearchEntryComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationSearchViewHolder {
        val binding = LocationSearchResultItemBinding.inflate(
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


class LocationSearchViewHolder(private val binding: LocationSearchResultItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationSearchEntry, parentViewModel: WeatherApplicationViewModel) {
            binding.searchEntry = item
            binding.parentViewModel = parentViewModel
            binding.executePendingBindings()
        }
    }


class LocationSearchEntryComparator : DiffUtil.ItemCallback<LocationSearchEntry>() {
    override fun areItemsTheSame(
        oldItem: LocationSearchEntry,
        newItem: LocationSearchEntry
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: LocationSearchEntry,
        newItem: LocationSearchEntry
    ): Boolean {
        return oldItem == newItem
    }
}