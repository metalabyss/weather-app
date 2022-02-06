package io.farafonova.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.LocationSearchResultItemBinding

class LocationSearchRecyclerViewAdapter(private val searchResults: List<LocationSearchEntry>) :
    RecyclerView.Adapter<LocationSearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationSearchViewHolder {
        val binding = LocationSearchResultItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LocationSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationSearchViewHolder, position: Int) {
        if (position < searchResults.size) {
            val searchEntry = searchResults[position]
            holder.binding.tvLocationName.text = searchEntry.name
            holder.binding.tvLocationState.text = String.format(
                holder.itemView.context.getString(R.string.searched_location_state_template_string),
                searchEntry.state,
                searchEntry.country
            )
            holder.binding.tbFavorite.isChecked = searchEntry.isSelected
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }
}

class LocationSearchViewHolder(val binding: LocationSearchResultItemBinding) :
    RecyclerView.ViewHolder(binding.root)