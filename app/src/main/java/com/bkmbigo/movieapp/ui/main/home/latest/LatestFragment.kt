package com.bkmbigo.movieapp.ui.main.home.latest

import android.app.PendingIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.NavDeepLinkRequest
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.data.repository.MovieRepositoryImpl
import com.bkmbigo.movieapp.databinding.FragmentLatestBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import com.bkmbigo.movieapp.ui.adapter.MovieBottomSheetAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LatestFragment : Fragment(), CardMovieAdapter.MovieClickListener {

    private var _binding: FragmentLatestBinding? = null
    private val binding get() = _binding!!

    private lateinit var latestViewModel: LatestViewModel
    private val cardMovieAdapter = CardMovieAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLatestBinding.inflate(inflater, container, false)

        val _latestViewModel by viewModels<LatestViewModel> {
            LatestViewModel.LatestViewModelFactory(MovieRepositoryImpl(getString(R.string.omdb_api_key)))
        }
        latestViewModel = _latestViewModel

        binding.recyclerView.adapter = cardMovieAdapter
        latestViewModel.latestResults.observe(viewLifecycleOwner) { list ->
            cardMovieAdapter.submitList(list)
        }

        observeError()


        return binding.root
    }

    private fun onError(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
    }

    private fun observeError() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                latestViewModel.errorMessage.collect { error ->
                    onError(error)
                }
            }
        }
    }

    override fun onMovieClicked(movie: Movie) {
        latestViewModel.changeLoading(true)
        if (movie.imdbID != null) {
            val movieBottomSheetAdapter = MovieBottomSheetAdapter(movie)
            movieBottomSheetAdapter.show(
                requireActivity().supportFragmentManager,
                MovieBottomSheetAdapter.TAG
            )
            latestViewModel.changeLoading(false)
        } else {
            //Implement Search Function --> Navigation with arguments
        }


    }

    private fun generateDeepLink(title: String){

    }
}