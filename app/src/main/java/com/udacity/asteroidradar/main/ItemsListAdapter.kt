package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import androidx.fragment.app.Fragment
import com.udacity.asteroidradar.databinding.ListViewItemBinding

// Bind our recycler view to this adapter

// create an ItemListAdapter' class that extends a..
// RecyclerView ListAdapter with DiffCallback. Have it use a custom..
// ListViewAdapter.AsteroidPropertyViewHolder to present a list of <AsteroidProperty> objects

class ItemsListAdapter (private val onClickListener: OnClickListener) : ListAdapter<Asteroid, ItemsListAdapter.AsteroidViewHolder>(DiffCallback) {
    // Create inner view holderclass
    // Use binding variable to bind Asteroid properties to the layout
    class AsteroidViewHolder(private var binding: ListViewItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        // A method that takes Mars properties and sets it in the binding class
        fun bind(asteroidProperty: Asteroid) {
            binding.asteroid = asteroidProperty

            // Dynamically set the status icon description
            if(asteroidProperty.isPotentiallyHazardous){
                binding.imageView.contentDescription = "Potentially hazardous asteroid image"
            } else{
                binding.imageView.contentDescription = "Not hazardous asteroid image"

            }
            binding.executePendingBindings()
        }
    }

// Create the DiffCallback companion object and override its two..
// required areItemsTheSame() methods:
// We want to compare Asteroid

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            // Use standard equality operator
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            AsteroidViewHolder {
        // Needs to return this after inflation
        return AsteroidViewHolder(ListViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroidProperty = getItem(position)
        // Call on click listener here passing in the asteroidProperty
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroidProperty)
        }
        holder.bind(asteroidProperty)
    }

    // create an internal OnClickListener class with a lambda in its constructor that..
// initializes a matching onClick function:
    class OnClickListener(val clickListener: (asteroidProperty: Asteroid) -> Unit) {
        fun onClick(asteroidProperty:Asteroid) = clickListener(asteroidProperty)
    }
}
