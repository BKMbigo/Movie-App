package com.bkmbigo.movieapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bkmbigo.movieapp.R
import com.bkmbigo.movieapp.databinding.CardMovieBinding
import com.bkmbigo.movieapp.domain.model.Movie
import com.bumptech.glide.Glide

class CardMovieAdapter(
    private val movieClickListener: MovieClickListener,
) : ListAdapter<Movie, CardMovieAdapter.CardMovieViewHolder>(CardMovieDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardMovieViewHolder {
        val binding = CardMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardMovieViewHolder.create(binding, movieClickListener)
    }

    override fun onBindViewHolder(holder: CardMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CardMovieViewHolder(
        private val binding: CardMovieBinding,
        private val movieClickListener: MovieClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(movie: Movie) {
            binding.tvTitle.text = movie.title
            Glide.with(binding.root.context)
                .load(movie.posterURL)
                .placeholder(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_movie))
                .into(binding.ivPoster)


            //Open Bottom Sheet
            binding.root.setOnClickListener {
                movieClickListener.onMovieClicked(movie)
            }
        }

        companion object {
            fun create(
                binding: CardMovieBinding,
                movieClickListener: MovieClickListener
            ): CardMovieViewHolder {
                return CardMovieViewHolder(binding, movieClickListener)
            }
        }
    }

    interface MovieClickListener {
        fun onMovieClicked(movie: Movie)
    }

    class CardMovieDiffUtil : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.imdbID == newItem.imdbID &&
                    oldItem.title == newItem.title
        }

    }
}