package com.bkmbigo.movieapp.ui.main.home.latest

import androidx.lifecycle.*
import com.bkmbigo.movieapp.api.PSAripsAPI
import com.bkmbigo.movieapp.api.dto.PSAripsFeed
import com.bkmbigo.movieapp.api.dto.PSAItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LatestViewModel(
    private val psaripsAPI: PSAripsAPI
): ViewModel() {

    private val _latestResults = MutableLiveData<List<PSAItem?>>()
    val latestResults: LiveData<List<PSAItem?>> = _latestResults

    private val _errorMessage = Channel<String>()
    val errorMessage get() = _errorMessage.receiveAsFlow()

    suspend fun getLatestResults(){
        withContext(Dispatchers.IO){
            val call = psaripsAPI.getPSAripsFeed()
            call.enqueue(object : Callback<PSAripsFeed>{
                override fun onResponse(call: Call<PSAripsFeed>, response: Response<PSAripsFeed>) {
//                    try {
                        if (response.body() != null) {
                            val feed = response.body()
                            val items = feed!!.channel.items

                            val list = ArrayList<PSAItem>()
                            items.forEach {
                                val _item = PSAItem(it.title, it.link, it.description, it.category)
                                list.add(_item)
                            }

                            _latestResults.value = list
                        }
//                    }catch(e: Exception){
//                        viewModelScope.launch {
//                            _errorMessage.send(e.message!!)
//                        }
//                    }
                }

                override fun onFailure(call: Call<PSAripsFeed>, t: Throwable) {
                    viewModelScope.launch {
                        _errorMessage.send(t.message!!)
                    }
                }

            })
        }
    }

    init {
        viewModelScope.launch {
            getLatestResults()
        }
    }


    class LatestViewModelFactory(private val psaripsAPI: PSAripsAPI): ViewModelProvider.NewInstanceFactory(){
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LatestViewModel(psaripsAPI) as T
    }
}