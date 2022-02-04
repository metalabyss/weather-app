package io.farafonova.weatherapp

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import io.farafonova.weatherapp.databinding.FragmentLocationSearchBinding

class LocationSearchFragment : Fragment() {
    private var binding: FragmentLocationSearchBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationSearchBinding.inflate(layoutInflater, container, false)
        setupToolbar(binding!!.searchAppBar)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
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
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
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