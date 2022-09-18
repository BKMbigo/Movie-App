package com.bkmbigo.movieapp.ui.adapter.moviebottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.data.repository.MovieRepositoryImpl
import com.bkmbigo.movieapp.databinding.BottomSheetMovieBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MovieBottomSheetAdapter(
    private val movie: Movie
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetMovieBinding? = null
    private val binding get() = _binding!!

    private lateinit var movieBottomSheetViewModel: MovieBottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetMovieBinding.inflate(inflater, container, false)
        val databaseReference = FirebaseDatabase.getInstance().reference

        val _movieBottomSheetViewModel by viewModels<MovieBottomSheetViewModel> {
            MovieBottomSheetViewModel.MovieBottomSheetViewModelFactory(
                movie,
                MovieRepositoryImpl(databaseReference = databaseReference)
            )
        }
        movieBottomSheetViewModel = _movieBottomSheetViewModel

        observeState()

        movieBottomSheetViewModel.movie.observe(viewLifecycleOwner){ movie ->
            bindMovie(movie)
        }

        return binding.root
    }

    fun bindMovie(movie: Movie = this.movie) {
        binding.tvTitle.text = movie.title
        binding.tvActors.text =
            binding.root.context.getString(R.string.label_actors_value, movie.actors.joinToString())
        binding.tvPlot.text = movie.plot

        Glide.with(binding.root.context)
            .load(movie.posterURL)
            .into(binding.ivPoster)

        binding.chipType.text = movie.type.name

        //Add Genre Chips

        binding.btFavorite.icon = if(movie.favorite) ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_favorite)
        //set Action For Adding to Favorite
        binding.btFavorite.setOnClickListener {
            movieBottomSheetViewModel.toggleFavorite()
        }

        binding.btBookmark.icon = if(movie.bookmarked) ContextCompat.getDrawable(requireContext(), R.drawable.ic_bookmark) else ContextCompat.getDrawable(requireContext(), R.drawable.ic_no_bookmark)
        //set Action for Bookmark
        binding.btBookmark.setOnClickListener {
            movieBottomSheetViewModel.toggleBookmark()
        }

        binding.chipWatchList.isChecked = movie.watched
        //set Action for Add to Watch List
        binding.chipWatchList.setOnCheckedChangeListener { _, _ ->
            movieBottomSheetViewModel.toggleWatchList()
        }

        binding.chipDownloadList.isChecked = movie.onDownloadList
        //set Action for Add to Download List
        binding.chipDownloadList.setOnCheckedChangeListener { _, _ ->
            movieBottomSheetViewModel.toggleDownloadList()
        }

    }

    private fun onError(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun observeState(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                movieBottomSheetViewModel.loading.collect{ loading ->
                    binding.progressHorizontal.visibility = if(loading) View.VISIBLE else View.GONE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                movieBottomSheetViewModel.error.collect{ error ->
                    onError(error)
                }
            }
        }
    }

    companion object {
        const val TAG = "MovieBottomSheet"
    }
}