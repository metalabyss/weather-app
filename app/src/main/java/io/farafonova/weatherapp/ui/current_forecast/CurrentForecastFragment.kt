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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentCurrentForecastBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory
import kotlinx.coroutines.launch


class CurrentForecastFragment : Fragment() {
    private var _binding: FragmentCurrentForecastBinding? = null
    private val binding get() = _binding!!


    private val viewModel: CurrentForecastViewModel by activityViewModels {
        val app = activity?.application as WeatherApplication
        WeatherApplicationViewModelFactory(
            app.repository,
            app.refreshWorkInfo
        )
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
                    viewModel.getDailyForecastsForSpecificLocation(latitude, longitude)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentForecastBinding
            .inflate(layoutInflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            isLongTaskRunning = viewModel.isLongTaskRunning
            forecast = viewModel.singleDetailedForecast
            currentForecastAppBar.setNavigationOnClickListener {
                viewModel.singleDetailedForecast.value = null
                parentFragmentManager.popBackStack()
            }
        }

        val hourlyForecastAdapter = HourlyForecastRecyclerViewAdapter(viewModel::isItLight)
        binding.rvHourlyForecast.adapter = hourlyForecastAdapter

        val dailyForecastAdapter = DailyForecastRecyclerViewAdapter()
        binding.rvDailyForecast.adapter = dailyForecastAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    context?.let { c -> MaterialAlertDialogBuilder(c)
                        .setMessage(it)
                        .setPositiveButton(R.string.button_text_ok) { dialog, which -> dialog.dismiss() }
                        .show() }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hourlyForecast.collect {
                    hourlyForecastAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyForecasts.collect {
                    dailyForecastAdapter.submitList(it)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}