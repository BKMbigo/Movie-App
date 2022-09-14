package com.bkmbigo.movieapp.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.api.dto.ParticularMovieDto
import com.bkmbigo.movieapp.databinding.BottomSheetMovieBinding
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MovieBottomSheetAdapter(
    private val particularMovieDto: ParticularMovieDto,
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
        binding.tvTitle.text = particularMovieDto.title
        binding.tvActors.text = binding.root.context.getString(R.string.label_actors_value, particularMovieDto.actors)
        binding.tvPlot.text = particularMovieDto.plot

        Glide.with(binding.root.context)
            .load(particularMovieDto.poster)
            .into(binding.ivPoster)

        binding.chipType.text = particularMovieDto.type

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