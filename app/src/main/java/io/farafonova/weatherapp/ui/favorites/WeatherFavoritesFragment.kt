package io.farafonova.weatherapp.ui.favorites

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.R
import io.farafonova.weatherapp.WeatherApplication
import io.farafonova.weatherapp.databinding.FragmentWeatherFavoritesBinding
import io.farafonova.weatherapp.ui.WeatherApplicationViewModel
import io.farafonova.weatherapp.ui.WeatherApplicationViewModelFactory
import io.farafonova.weatherapp.ui.search.LocationSearchFragment


class WeatherFavoritesFragment : Fragment() {
    private var binding: FragmentWeatherFavoritesBinding? = null
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
        binding!!.lifecycleOwner = viewLifecycleOwner
        binding!!.weatherViewModel = viewModel

        binding!!.appBar.inflateMenu(R.menu.toolbar_menu)
        binding!!.appBar.setOnMenuItemClickListener {
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

        binding!!.recyclerView.setHasFixedSize(true)
        val adapter = WeatherFavoritesRecyclerViewAdapter()
        binding!!.recyclerView.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) {
            it?.let { adapter.submitList(it) }
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}