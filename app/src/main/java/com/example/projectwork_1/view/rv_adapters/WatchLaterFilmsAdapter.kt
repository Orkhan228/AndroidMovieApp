package com.example.projectwork_1.view.rv_adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.databinding.WatchLaterFilmItemBinding
import com.example.projectwork_1.entity.WatchLaterNotification
import com.example.projectwork_1.utils.WatchLaterFilmDiffUtil
import com.example.projectwork_1.view.rv_viewholders.WatchLaterFilmsViewHolder

class WatchLaterFilmsAdapter(private val onWatchLaterClickListener: OnWatchLaterClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var itemsWL = mutableListOf<WatchLaterNotification>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val binding =
            WatchLaterFilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WatchLaterFilmsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is WatchLaterFilmsViewHolder -> {
                //привязываем данные
                holder.bind(itemsWL[position])
                //ставим слушатель на нажатие на элемент списка, при клике передается позиция элемента и картинка, реализация будет при создании адаптера
                holder.itemView.setOnClickListener {
                    onWatchLaterClickListener.click(
                        itemsWL[position].film,
                        holder.poster
                    )
                }
                holder.buttonRefactor.setOnClickListener {
                    onWatchLaterClickListener.onEditClick(itemsWL[position])
                }
                holder.buttonCancel.setOnClickListener {
                    onWatchLaterClickListener.onDeleteClick(itemsWL[position])
                }
            }
        }

    }

    override fun getItemCount(): Int = itemsWL.size

    fun addItems(list: MutableList<WatchLaterNotification>) {
        //DiffUtil из дополнительного задания реализован
        val oldData = itemsWL
        val newData = list.toMutableList()
        val diff = WatchLaterFilmDiffUtil(oldData, newData)
        val diffRes = DiffUtil.calculateDiff(diff)
        itemsWL = newData
        diffRes.dispatchUpdatesTo(this)
    }


    interface OnWatchLaterClickListener {
        fun click(film: Film, posterView: ImageView)
        fun onEditClick(watchLaterNotification: WatchLaterNotification)
        fun onDeleteClick(watchLaterNotification: WatchLaterNotification)
    }

}