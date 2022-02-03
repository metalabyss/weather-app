package io.farafonova.weatherapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.farafonova.weatherapp.databinding.FragmentWeatherFavoritesBinding


class WeatherFavoritesFragment : Fragment() {
    private var binding: FragmentWeatherFavoritesBinding? = null

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

        (activity as AppCompatActivity).setSupportActionBar(binding!!.appBar)

        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = WeatherFavoritesRecyclerViewAdapter(
            listOf(
                FavoritesWeatherEntry("Novosibirsk", -8),
                FavoritesWeatherEntry("Krasnoyarsk", -10),
                FavoritesWeatherEntry("Sochi", +5)
            )
        )
        binding!!.recyclerView.adapter = adapter
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}