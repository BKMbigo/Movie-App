package com.bkmbigo.movieapp.ui.main.home.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.data.repository.MovieRepositoryImpl
import com.bkmbigo.movieapp.databinding.FragmentWatchListBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import com.bkmbigo.movieapp.ui.adapter.moviebottomsheet.MovieBottomSheetAdapter
import com.bkmbigo.movieapp.utils.WebApiCallback
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class WatchListFragment: Fragment(), CardMovieAdapter.MovieClickListener {

    private var _binding: FragmentWatchListBinding? = null
    private val binding get()= _binding!!

    private lateinit var watchListViewModel: WatchListViewModel
    private val cardMovieAdapter = CardMovieAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchListBinding.inflate(inflater, container, false)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val movieRepository = MovieRepositoryImpl(getString(R.string.omdb_api_key), databaseReference)
        val _watchListViewModel by viewModels<WatchListViewModel> { WatchListViewModel.WatchListViewModelFactory(movieRepository) }
        watchListViewModel= _watchListViewModel


        setUpChips()
        setUpSwipeRefreshLayout()
        observeError()

        binding.recyclerView.adapter = cardMovieAdapter
        watchListViewModel.watchList.observe(viewLifecycleOwner){ movies ->
            cardMovieAdapter.submitList(movies)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when(watchListViewModel.state.value){
            WatchListViewModel.WatchListState.All -> binding.chipGroup.clearCheck()
            WatchListViewModel.WatchListState.Movie -> binding.chipGroup.check(binding.chipMovie.id)
            WatchListViewModel.WatchListState.Series -> binding.chipGroup.check(binding.chipSeries.id)
            WatchListViewModel.WatchListState.Episode -> binding.chipGroup.check(binding.chipEpisode.id)
            WatchListViewModel.WatchListState.Game -> binding.chipGroup.check(binding.chipGame.id)
        }
    }

    private fun observeError(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                watchListViewModel.error.collect{
                    showError(it)
                }
            }
        }
    }

    private fun showError(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setUpSwipeRefreshLayout(){
        binding.srl.setOnRefreshListener {
            watchListViewModel.refresh()
        }

        //Observe State
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                watchListViewModel.loading.collect{ loading ->
                    binding.srl.isRefreshing = loading
                }
            }
        }
    }
    private fun setUpChips(){
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if(checkedIds.isEmpty()){
                watchListViewModel.changeState(WatchListViewModel.WatchListState.All)
            }else{
                if(checkedIds.contains(binding.chipMovie.id)){
                    watchListViewModel.changeState(WatchListViewModel.WatchListState.Movie)
                } else if(checkedIds.contains(binding.chipSeries.id)){
                    watchListViewModel.changeState(WatchListViewModel.WatchListState.Series)
                } else if(checkedIds.contains(binding.chipEpisode.id)){
                    watchListViewModel.changeState(WatchListViewModel.WatchListState.Episode)
                } else if(checkedIds.contains(binding.chipGame.id)){
                    watchListViewModel.changeState(WatchListViewModel.WatchListState.Game)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMovieClicked(movie: Movie) {
        if(movie.imdbID != null){
            val movieBottomSheetAdapter = MovieBottomSheetAdapter(movie)
            movieBottomSheetAdapter.show(requireActivity().supportFragmentManager, MovieBottomSheetAdapter.TAG)
        } else {
            showError("No IMDB ID Found")
        }
    }
}