package io.farafonova.weatherapp.ui.search

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentLocationSearchBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory
import kotlinx.coroutines.launch

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
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            locationSearchViewModel = viewModel
            isLongTaskRunning = viewModel.isLongTaskRunning

            queryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { viewModel.searchForLocations(query) }
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Here is where we are going to implement the filter logic
                    return false
                }
            }
            rvSearchResults.setHasFixedSize(true)
            searchAppBar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }
        }
        val adapter = LocationSearchRecyclerViewAdapter(viewModel)
        binding.rvSearchResults.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchResult.collect {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchView.post {
            binding.searchView.requestFocus()
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchView, 0)
        }
    }
}