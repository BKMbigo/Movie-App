package com.bkmbigo.movieapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.api.dto.SearchMovieDto
import com.bkmbigo.movieapp.databinding.CardMovieBinding
import com.bumptech.glide.Glide

class CardMovieAdapter: ListAdapter<SearchMovieDto, CardMovieAdapter.CardMovieViewHolder>(CardMovieDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMovieViewHolder {
        val binding = CardMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardMovieViewHolder.create(binding)
    }

    override fun onBindViewHolder(holder: CardMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CardMovieViewHolder(private val binding: CardMovieBinding)
    : RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(searchMovieDto: SearchMovieDto){
            binding.tvTitle.text = "${searchMovieDto.title}(${searchMovieDto.year})"
            Glide.with(binding.root.context)
                .load(searchMovieDto.posterURL)
                .placeholder(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_movie))
                .into(binding.ivPoster)
        }

        companion object{
            fun create(binding: CardMovieBinding): CardMovieViewHolder{
                return CardMovieViewHolder(binding)
            }
        }
    }

    class CardMovieDiffUtil: DiffUtil.ItemCallback<SearchMovieDto>(){
        override fun areItemsTheSame(oldItem: SearchMovieDto, newItem: SearchMovieDto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SearchMovieDto, newItem: SearchMovieDto): Boolean {
            return oldItem.imdbID == newItem.imdbID &&
                    oldItem.title == newItem.title
        }

    }
}