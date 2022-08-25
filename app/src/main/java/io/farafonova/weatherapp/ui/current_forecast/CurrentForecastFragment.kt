package io.farafonova.weatherapp.ui.current_forecast

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentCurrentForecastBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory
import kotlinx.coroutines.launch


class CurrentForecastFragment : Fragment() {
    private lateinit var binding: FragmentCurrentForecastBinding
    private val viewModel: WeatherApplicationViewModel by activityViewModels {
        WeatherApplicationViewModelFactory((activity?.application as WeatherApplication).datasourceManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("SELECTED_FAVORITE") { _, bundle ->
            val latitude = bundle.getDouble("LOCATION_LATITUDE")
            val longitude = bundle.getDouble("LOCATION_LONGITUDE")

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.getCurrentForecastForSpecificLocation(latitude, longitude)
                    viewModel.getHourlyForecastForSpecificLocation(latitude, longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentForecastBinding.inflate(layoutInflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            isLongTaskRunning = viewModel.isLongTaskRunning
            forecast = viewModel.singleDetailedForecast
            currentForecastAppBar.setNavigationOnClickListener {
                viewModel.singleDetailedForecast.value = null
                parentFragmentManager.popBackStack()
            }
        }

        val hourlyForecastAdapter = HourlyForecastRecyclerViewAdapter()
        binding.rvHourlyForecast.adapter = hourlyForecastAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hourlyForecast.collect {
                    hourlyForecastAdapter.submitList(it)
                }
            }
        }

        return binding.root
    }

}