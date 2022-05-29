package io.farafonova.weatherapp.ui.search

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentLocationSearchBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory

class LocationSearchFragment : Fragment() {
    private val viewModel: WeatherApplicationViewModel by activityViewModels {
        WeatherApplicationViewModelFactory((activity?.application as WeatherApplication).datasourceManager)
    }

    private lateinit var binding: FragmentLocationSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationSearchBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.apply {
            locationSearchViewModel = viewModel
            isLongTaskRunning = viewModel.isLongTaskRunning
            rvSearchResults.setHasFixedSize(true)
        }
        val adapter = LocationSearchRecyclerViewAdapter(viewModel)
        binding.rvSearchResults.adapter = adapter
        setupToolbar(binding.searchAppBar)

        viewModel.searchResult.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
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

    private fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            inflateMenu(R.menu.location_search_toolbar_menu)
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        }

        val searchView = SearchView(context)

        searchView.apply {
            isIconifiedByDefault = false
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            gravity = Gravity.FILL_HORIZONTAL
            requestFocusFromTouch()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchForLocations(query) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Here is where we are going to implement the filter logic
                return false
            }

        })

        toolbar.menu.findItem(R.id.search).apply {
            actionView = searchView
        }
    }
}