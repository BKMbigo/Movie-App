package com.bkmbigo.movieapp.ui.main.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.data.repository.MovieRepositoryImpl
import com.bkmbigo.movieapp.databinding.FragmentSearchBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import com.bkmbigo.movieapp.ui.adapter.moviebottomsheet.MovieBottomSheetAdapter
import com.bkmbigo.movieapp.utils.WebApiCallback
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*


class SearchFragment
    : Fragment(), CardMovieAdapter.MovieClickListener{

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel
    private val cardMovieAdapter = CardMovieAdapter(this)

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val _searchViewModel by viewModels<SearchViewModel> {
            SearchViewModel.HomeViewModelFactory(MovieRepositoryImpl(getString(R.string.omdb_api_key)))
        }
        searchViewModel = _searchViewModel


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeStates()

        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            searchMovie(text.toString())
        }
        binding.srl.setOnRefreshListener {
            lifecycleScope.launch(Dispatchers.IO){
                searchMovie(binding.etSearch.text.toString())
            }
        }
        binding.recyclerView.adapter = cardMovieAdapter

        binding.chipMovie.setOnCheckedChangeListener { _, _ -> searchMovie(binding.etSearch.text.toString()) }
        binding.chipSeries.setOnCheckedChangeListener { _, _ -> searchMovie(binding.etSearch.text.toString()) }
        binding.chipEpisode.setOnCheckedChangeListener { _, _ -> searchMovie(binding.etSearch.text.toString()) }
        binding.chipGame.setOnCheckedChangeListener { _, _ -> searchMovie(binding.etSearch.text.toString()) }

        searchViewModel.movieResults.observe(viewLifecycleOwner){ results->
            cardMovieAdapter.submitList(results)
            binding.rlNone.visibility = if(results.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun searchMovie(query: String, type: String = getType()){
        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.IO){
            delay(1000L)
            searchViewModel.searchMovies(query, type)
        }
    }

    private fun observeStates(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                searchViewModel.loading.collect{ loading ->
                    binding.srl.isRefreshing = loading
                }
            }
        }

    }

    private fun getType(): String{
        return if(binding.chipMovie.isChecked){
            "movie"
        }else if(binding.chipSeries.isChecked){
            "series"
        }else if(binding.chipGame.isChecked){
            "game"
        }else if(binding.chipEpisode.isChecked){
            "episode"
        }else{
            "null"
        }
    }

    fun onError(message: String) = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMovieClicked(movie: Movie) {
        if(movie.imdbID != null){
            searchViewModel.changeLoading(true)
            lifecycleScope.launch(Dispatchers.IO) {
                val callback = object: WebApiCallback<Movie>{
                    override fun onResponse(data: Movie) {
                        searchViewModel.changeLoading(false)
                        val movieBottomSheetAdapter = MovieBottomSheetAdapter(data)
                        movieBottomSheetAdapter.show(requireActivity().supportFragmentManager, MovieBottomSheetAdapter.TAG)
                    }

                    override fun onFailure(e: Exception?) {
                        searchViewModel.changeLoading(false)
                        onError(e?.message?:"Error parsing response from API")
                    }

                }
                searchViewModel.getParticularMovie(movie, callback)
            }
        } else {
            binding.etSearch.setText(movie.title)
        }
    }




    companion object{
        const val SEARCH_TITLE_KEY = "com.bkmbigo.movieapp.ui.search.searchkey"
    }
}