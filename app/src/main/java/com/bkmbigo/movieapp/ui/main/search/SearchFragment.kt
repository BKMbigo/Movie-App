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
import com.bkmbigo.movieapp.api.OmdbApi
import com.bkmbigo.movieapp.api.dto.ParticularMovieDto
import com.bkmbigo.movieapp.databinding.FragmentSearchBinding
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import com.bkmbigo.movieapp.ui.adapter.MovieBottomSheetAdapter
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchFragment : Fragment(), CardMovieAdapter.MovieClickListener {

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
            SearchViewModel.HomeViewModelFactory(getString(R.string.omdb_api_key), OmdbApi.getClient())
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMovieClicked(imdbID: String?, title: String?, type: String?) {
        searchViewModel.changeLoading(true)
        val call = OmdbApi.getClient().getParticularMovie(getString(R.string.omdb_api_key), imdbID!!, type)
        call.enqueue(object: Callback<ParticularMovieDto>{
            override fun onResponse(
                call: Call<ParticularMovieDto>,
                response: Response<ParticularMovieDto>
            ) {
                val movieBottomSheetAdapter = response.body()?.let { MovieBottomSheetAdapter(it) }
                movieBottomSheetAdapter?.show(requireActivity().supportFragmentManager, MovieBottomSheetAdapter.TAG)
                searchViewModel.changeLoading(false)
            }

            override fun onFailure(call: Call<ParticularMovieDto>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }
}