package com.bkmbigo.movieapp.ui.main.home.dowloadlist

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
import com.bkmbigo.movieapp.databinding.FragmentDownloadListBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.domain.repository.MovieRepository
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import com.bkmbigo.movieapp.ui.adapter.moviebottomsheet.MovieBottomSheetAdapter
import com.bkmbigo.movieapp.ui.main.home.watchlist.WatchListViewModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class DownloadListFragment: Fragment(), CardMovieAdapter.MovieClickListener {

    private var _binding: FragmentDownloadListBinding? = null
    private val binding get()= _binding!!

    private lateinit var downloadListViewModel: DownloadListViewModel
    private val cardMovieAdapter = CardMovieAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadListBinding.inflate(inflater, container, false)
        val databaseReference = FirebaseDatabase.getInstance().reference

        val movieRepository: MovieRepository = MovieRepositoryImpl(getString(R.string.omdb_api_key), databaseReference)
        val _downloadListViewModel by viewModels<DownloadListViewModel> { DownloadListViewModel.DownloadListViewModelFactory(movieRepository) }
        downloadListViewModel = _downloadListViewModel

        setUpChips()
        setUpSwipeRefreshLayout()
        observeError()

        binding.recyclerView.adapter = cardMovieAdapter
        downloadListViewModel.downloadList.observe(viewLifecycleOwner){ movies ->
            cardMovieAdapter.submitList(movies)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when(downloadListViewModel.state.value){
            DownloadListViewModel.DownloadListState.All -> binding.chipGroup.clearCheck()
            DownloadListViewModel.DownloadListState.Movie -> binding.chipGroup.check(binding.chipMovie.id)
            DownloadListViewModel.DownloadListState.Series -> binding.chipGroup.check(binding.chipSeries.id)
            DownloadListViewModel.DownloadListState.Episode -> binding.chipGroup.check(binding.chipEpisode.id)
            DownloadListViewModel.DownloadListState.Game -> binding.chipGroup.check(binding.chipGame.id)
        }
    }

    private fun observeError(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                downloadListViewModel.error.collect{
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
            downloadListViewModel.refreshDownloadList()
        }

        //Observe State
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                downloadListViewModel.loading.collect{ loading ->
                    binding.srl.isRefreshing = loading
                }
            }
        }
    }

    private fun setUpChips(){
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if(checkedIds.isEmpty()){
                downloadListViewModel.changeState(DownloadListViewModel.DownloadListState.All)
            }else{
                if(checkedIds.contains(binding.chipMovie.id)){
                    downloadListViewModel.changeState(DownloadListViewModel.DownloadListState.Movie)
                } else if(checkedIds.contains(binding.chipSeries.id)){
                    downloadListViewModel.changeState(DownloadListViewModel.DownloadListState.Series)
                } else if(checkedIds.contains(binding.chipEpisode.id)){
                    downloadListViewModel.changeState(DownloadListViewModel.DownloadListState.Episode)
                } else if(checkedIds.contains(binding.chipGame.id)){
                    downloadListViewModel.changeState(DownloadListViewModel.DownloadListState.Game)
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