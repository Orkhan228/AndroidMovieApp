package com.example.projectwork_1.view.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.projectwork_1.databinding.FilmItemBinding
import com.example.projectwork_1.data.entity.Film
import com.example.projectwork_1.utils.FilmDiffUtil
import com.example.projectwork_1.view.rv_viewholders.FilmViewHolder

class FilmListAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //текущий список фильмов, который отображается в RecyclerView
    private var items = mutableListOf<Film>()

    //В пошаговом решении этот метод был нужен, но в моем проекте нет, так как у меня во вью модел в методе
    //PutCategoryProperty() очищается список allFilms и filmsListLiveData подписан на него, получается при изменении списка allFilms
    //те кто подписаны на filmsListLiveData сразу получают изменения

//    fun clearItems() {
//        items.clear()
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): FilmViewHolder {
        val binding =
            FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmViewHolder(binding)
    }

    //Вызывается когда нужно заполнить элемент данными
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is FilmViewHolder -> {
                //привязываем данные
                holder.bind(items[position])
                //ставим слушатель на нажатие на элемент списка, при клике передается позиция элемента и картинка, реализация будет при создании адаптера
                holder.itemView.setOnClickListener {
                    clickListener.click(
                        items[position],
                        holder.poster
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(list: MutableList<Film>) {
        //DiffUtil из дополнительного задания реализован
        val oldData = items
        val newData = list.toMutableList()
        val diff = FilmDiffUtil(oldData, newData)
        val diffRes = DiffUtil.calculateDiff(diff)
        items = newData
        diffRes.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun click(film: Film, posterView: ImageView)
    }
}