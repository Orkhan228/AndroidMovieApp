package com.example.projectwork_1.view.rv_viewholders

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectwork_1.databinding.FilmItemBinding
import com.example.projectwork_1.domain.Film

class FilmViewHolder(val bindingRecycler: FilmItemBinding) : RecyclerView.ViewHolder(bindingRecycler.root) {

    private val title: TextView = bindingRecycler.title
    val poster: ImageView = bindingRecycler.poster
    private val description: TextView = bindingRecycler.description
    private val ratingView = bindingRecycler.ratingDonut

    fun bind(film: Film) {
        title.text = film.title
        //создаем для каждого постера элемента свой transitionName
        poster.transitionName = "poster_${film.title}"
        Glide.with(itemView)
            .load(film.poster)
            .centerCrop()
            .into(poster)
        description.text = film.description
        ratingView.setProgressAnimated((film.rating * 10).toInt())
    }
}