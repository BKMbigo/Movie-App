package com.bkmbigo.movieapp.ui.main.home.latest

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
import com.bkmbigo.movieapp.api.OmdbApi
import com.bkmbigo.movieapp.api.PSAripsAPI
import com.bkmbigo.movieapp.api.dto.ParticularMovieDto
import com.bkmbigo.movieapp.api.dto.SearchMovieDto
import com.bkmbigo.movieapp.databinding.FragmentLatestBinding
import com.bkmbigo.movieapp.ui.adapter.CardMovieAdapter
import com.bkmbigo.movieapp.ui.adapter.MovieBottomSheetAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LatestFragment: Fragment(), CardMovieAdapter.MovieClickListener {

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

        val _latestViewModel by viewModels<LatestViewModel>{
            LatestViewModel.LatestViewModelFactory(PSAripsAPI.getClient())
        }
        latestViewModel = _latestViewModel

        binding.recyclerView.adapter = cardMovieAdapter
        latestViewModel.latestResults.observe(viewLifecycleOwner){ list ->
            val cardList = ArrayList<SearchMovieDto>()
            list.forEach { psaItem ->
                var description = psaItem!!.description
                description = description.substringAfter("src=").substringAfter('"').substringBefore('"').toString()

                val searchMovieDto = SearchMovieDto(psaItem.title, null, null, null, description)
                cardList.add(searchMovieDto)
            }
            cardMovieAdapter.submitList(cardList)
        }

        observeError()


        return binding.root
    }

    private fun onError(error: String){
        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
    }

    private fun observeError(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                latestViewModel.errorMessage.collect{ error ->
                    onError(error)
                }
            }
        }
    }

    override fun onMovieClicked(imdbID: String?, title: String?, type: String?) {
        if(imdbID !=  null){
            
        }else{
            val call = OmdbApi.getClient().getParticularMovie(getString(R.string.omdb_api_key), title = title)
            call.enqueue(object: Callback<ParticularMovieDto>{
                override fun onResponse(
                    call: Call<ParticularMovieDto>,
                    response: Response<ParticularMovieDto>
                ) {
                    val movieBottomSheetAdapter = MovieBottomSheetAdapter(response.body()!!)
                    movieBottomSheetAdapter.show(requireActivity().supportFragmentManager, MovieBottomSheetAdapter.TAG)
                }

                override fun onFailure(call: Call<ParticularMovieDto>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}