package io.farafonova.weatherapp.ui.favorites

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentWeatherFavoritesBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory
import io.farafonova.weatherapp.ui.current_forecast.CurrentForecastFragment
import io.farafonova.weatherapp.ui.search.LocationSearchFragment
import io.farafonova.weatherapp.ui.settings.SettingsFragment
import kotlinx.coroutines.launch


class WeatherFavoritesFragment : Fragment() {
    private var _binding: FragmentWeatherFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherFavoritesViewModel by activityViewModels {
        val app = activity?.application as WeatherApplication
        WeatherApplicationViewModelFactory(
            app.repository,
            app.refreshWorkInfo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWeatherFavoritesBinding.inflate(layoutInflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            weatherViewModel = viewModel
            listState = viewModel.favoritesState
            isLongTaskRunning = viewModel.isLongTaskRunning
            refreshWorkInfo = viewModel.refreshWorkInfo
        }

        binding.recyclerView.setHasFixedSize(true)

        val onFavoriteClickListener = { latitude: Double, longitude: Double ->
            val detailsFragment = CurrentForecastFragment()
            val bundle = Bundle().apply {
                putDouble("LOCATION_LATITUDE", latitude)
                putDouble("LOCATION_LONGITUDE", longitude)
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
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.START
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val weatherFavoritesViewHolder = (viewHolder as WeatherFavoritesViewHolder)
                val location = weatherFavoritesViewHolder.getLocation()
                location?.let {
                    viewModel.addOrRemoveFromFavorites(location, false)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoritesList.collect {
                    adapter.submitList(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect {
                    context?.let { c ->
                        MaterialAlertDialogBuilder(c)
                            .setMessage(it)
                            .setPositiveButton(R.string.button_text_ok) { dialog, which -> dialog.dismiss() }
                            .show()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.snackbarOptions.collect { options ->
                    context?.let {
                        val notificationText = options.notificationText.asString(it)
                        val actionText = options.actionText.asString(it)

                        val snackbar = Snackbar.make(
                            binding.favoritesCoordinatorLayout,
                            notificationText,
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setAction(actionText) { _ -> options.action.invoke() }
                        snackbar.show()
                    }
                }
            }
        }

        binding.favoritesSwipeRefreshLayout.setOnRefreshListener {
            if (viewModel.refreshWorkInfo.value?.state != WorkInfo.State.RUNNING
                && !(viewModel.isLongTaskRunning.value)) {
                viewModel.refreshForecasts()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLongTaskRunning.collect { isRunning ->
                    val item = binding.appBar.menu.findItem(R.id.action_refresh)
                    if (item != null) {
                        item.isEnabled = !isRunning
                    }
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBar.addMenuProvider(object : MenuProvider {
            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_weather_favorites, menu)
                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, LocationSearchFragment())
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    R.id.action_settings -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.container, SettingsFragment())
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    R.id.action_refresh -> {
                        if (menuItem.isEnabled) {
                            viewModel.refreshForecasts()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}