package io.farafonova.weatherapp.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentWeatherFavoritesBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory
import io.farafonova.weatherapp.ui.current_forecast.CurrentForecastFragment
import io.farafonova.weatherapp.ui.search.LocationSearchFragment
import kotlinx.coroutines.launch


class WeatherFavoritesFragment : Fragment() {
    private lateinit var binding: FragmentWeatherFavoritesBinding
    private val viewModel: WeatherApplicationViewModel by activityViewModels {
        WeatherApplicationViewModelFactory((activity?.application as WeatherApplication).datasourceManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWeatherFavoritesBinding.inflate(layoutInflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            weatherViewModel = viewModel
            isLongTaskRunning = viewModel.isLongTaskRunning
        }

        binding.appBar.inflateMenu(R.menu.toolbar_menu)
        binding.appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container, LocationSearchFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }

        binding.recyclerView.setHasFixedSize(true)

        val onFavoriteClickListener = { latitude: Float, longitude: Float ->
            val detailsFragment = CurrentForecastFragment()
            val bundle = Bundle().apply {
                putFloat("LOCATION_LATITUDE", latitude)
                putFloat("LOCATION_LONGITUDE", longitude)
            }
            setFragmentResult("SELECTED_FAVORITE", bundle)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, detailsFragment)
                .addToBackStack(null)
                .commit()
            Unit
        }
        val adapter = WeatherFavoritesRecyclerViewAdapter(onFavoriteClickListener)
        binding.recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFavorites()?.collect {
                adapter.submitList(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.errorMessage.collect {
                context?.let { c -> MaterialAlertDialogBuilder(c)
                    .setMessage(it)
                    .setPositiveButton(R.string.button_text_ok) { dialog, which -> dialog.dismiss() }
                    .show() }
            }
        }
        return binding.root
    }
}