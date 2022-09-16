package com.bkmbigo.movieapp.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.databinding.BottomSheetMovieBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MovieBottomSheetAdapter(
    private val movie: Movie,
): BottomSheetDialogFragment() {

    private var _binding: BottomSheetMovieBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMovieBinding.inflate(inflater, container, false)

        bindMovie()

        return binding.root
    }

    private fun bindMovie(){
        binding.tvTitle.text = movie.title
        binding.tvActors.text = binding.root.context.getString(R.string.label_actors_value, movie.actors.joinToString())
        binding.tvPlot.text = movie.plot

        Glide.with(binding.root.context)
            .load(movie.posterURL)
            .into(binding.ivPoster)

        binding.chipType.text = movie.type.name

        //Add Genre Chips


        //set Action For Adding to Favorite

        //set Action for Bookmark

        //set Action for Add to Watch List

        //set Action for Add to Download List


    }

    companion object{
        const val TAG = "MovieBottomSheet"
    }
}