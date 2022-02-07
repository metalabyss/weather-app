package io.farafonova.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.LocationSearchResultItemBinding

class LocationSearchRecyclerViewAdapter :
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
        holder.binding.tvLocationName.text = searchEntry.name
        if (searchEntry.state.isBlank()) {
            holder.binding.tvLocationState.text = searchEntry.country
        } else {
            holder.binding.tvLocationState.text = String.format(
                holder.itemView.context.getString(R.string.searched_location_state_template_string),
                searchEntry.state,
                searchEntry.country
            )
        }
        holder.binding.tbFavorite.isChecked = searchEntry.isSelected
    }
}


class LocationSearchViewHolder(val binding: LocationSearchResultItemBinding) :
    RecyclerView.ViewHolder(binding.root)


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