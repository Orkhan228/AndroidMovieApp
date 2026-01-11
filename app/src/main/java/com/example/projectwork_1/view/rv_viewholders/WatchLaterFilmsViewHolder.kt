package com.example.projectwork_1.view.rv_viewholders

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectwork_1.databinding.WatchLaterFilmItemBinding
import com.example.projectwork_1.entity.WatchLaterNotification
import com.example.projectwork_1.utils.ApiConstantsApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//Отдельный вью холдер для экрана Посмотреть позже, так как верстка элементов отличается
class WatchLaterFilmsViewHolder(binding: WatchLaterFilmItemBinding) : RecyclerView.ViewHolder(binding.root) {


    private val title: TextView = binding.title
    private val remindTime: TextView = binding.remindTimeTv
    val poster: ImageView = binding.poster
    val buttonRefactor: Button = binding.buttonRefactor
    val buttonCancel: Button = binding.buttonCancel
    private val ratingView = binding.ratingDonut


    fun bind(watchLaterNotification: WatchLaterNotification) {
        //Чтобы время в миллисекундах, превратить в обычную дата и время
        val millis = watchLaterNotification.triggerTime
        val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
        val resultDate = sdf.format(Date(millis))


        title.text = watchLaterNotification.film.title
        remindTime.text = resultDate
        //создаем для каждого постера элемента свой transitionName
        poster.transitionName = "poster_${watchLaterNotification.film.title}"
        Glide.with(itemView)
            .load(ApiConstantsApp.IMAGES_URL + "w342" + watchLaterNotification.film.poster)
            .centerCrop()
            .into(poster)
        ratingView.setProgressAnimated((watchLaterNotification.film.rating * 10).toInt())
    }

}