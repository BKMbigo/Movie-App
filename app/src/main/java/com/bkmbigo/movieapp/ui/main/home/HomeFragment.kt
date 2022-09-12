package com.bkmbigo.movieapp.ui.main.home

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
import androidx.navigation.fragment.findNavController
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.api.OmdbApi
import com.bkmbigo.movieapp.databinding.FragmentHomeBinding
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import kotlinx.coroutines.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private val cardMovieAdapter = CardMovieAdapter()

    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val _homeViewModel by viewModels<HomeViewModel> {
            HomeViewModel.HomeViewModelFactory(getString(R.string.omdb_api_key), OmdbApi.getClient())
        }
        homeViewModel = _homeViewModel


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

        homeViewModel.movieResults.observe(viewLifecycleOwner){ results->
            cardMovieAdapter.submitList(results)
            binding.rlNone.visibility = if(results.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

    }

    private fun searchMovie(query: String, type: String = getType()){
        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.IO){
            delay(1000L)
            homeViewModel.searchMovies(query, type)
        }
    }

    private fun observeStates(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.loading.collect{ loading ->
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
}