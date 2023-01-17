package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import kotlinx.android.synthetic.main.list_view_item.view.*

@RequiresApi(Build.VERSION_CODES.N)
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        //The ViewModelProviders (plural) is deprecated.
        //ViewModelProviders.of(this, DevByteViewModel.Factory(activity.application)).get(DevByteViewModel::class.java)
        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)
        )[MainViewModel::class.java]
    }

    // set the adapter in the RecyclerView (the ListVIew.adapter in the binding object)
    // to a new ListViewAdapter
    private var itemsListAdapter: ItemsListAdapter = ItemsListAdapter(ItemsListAdapter.OnClickListener{ i->
        viewModel.displayAsteroidDetails(i)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = itemsListAdapter

        binding.root.findViewById<RecyclerView>(R.id.asteroid_recycler).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemsListAdapter
        }

        // Actual navigation
        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asteroids.observe(viewLifecycleOwner, Observer { asteroid ->
            asteroid.apply {
                itemsListAdapter?.submitList(this)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.changeFilter(
            when (item.itemId) {
                R.id.show_rent_menu -> {
                    Filter.TODAY_ASTEROIDS
                }
                R.id.show_all_menu -> {
                    Filter.WEEK_ASTEROIDS
                }
                else -> {
                    Filter.SAVED_ASTEROIDS
                }
            }
        )
        return true
    }
}
