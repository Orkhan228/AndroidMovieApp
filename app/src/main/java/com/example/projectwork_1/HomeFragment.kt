package com.example.projectwork_1

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Fade
import androidx.transition.Scene
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.android.parcel.Parcelize
import java.util.Locale


class HomeFragment : Fragment() {

    private val newDataBase = FilmsDatabase.dataBase
    lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var rootView: CoordinatorLayout

//    init {
//        exitTransition = MaterialFade().apply {
//            duration = 500
//            mode = MaterialFade.MODE_OUT
//            propagation = null
//        }
//
//        reenterTransition = MaterialFade().apply {
//            duration = 800
//            mode = MaterialFade.MODE_IN
//            propagation = null
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        searchView = view.findViewById<SearchView>(R.id.search_view)
        rootView = view.findViewById<CoordinatorLayout>(R.id.home_fragment_root)

        val adapter = FilmListAdapter(object : FilmListAdapter.OnItemClickListener {
            //При клике мы открываем фрагмент с деталями, передаем туда фильм на который мы нажали, и изображение
            override fun click(film: Film, posterView: ImageView) {
                (requireActivity() as MainActivity).launchDetFragment(film, posterView)
            }
        })
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val decorator = FilmListItemDecor(8)
        recyclerView?.addItemDecoration(decorator)


        adapter.addItems(newDataBase)

        //При нажатии на весь SearchView, чтобы производился поиск
        searchView.setOnClickListener {
            searchView.isIconified = false
        }


        //Слушатель на SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isEmpty()) {
                    adapter.addItems(newDataBase)
                    return true
                } else {
                    val result = newDataBase.filter {
                        it.title.lowercase(Locale.getDefault()).contains(
                            newText.lowercase(
                                Locale.getDefault()
                            )
                        )
                    }
                    adapter.addItems(result as MutableList<Film>)
                }
                return true
            }
        })


        AnimationHelper.performFragmentCircularRevealAnimation(rootView, requireActivity(), 1)


    }

}

class FilmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.title)
    val poster: ImageView = itemView.findViewById(R.id.poster)
    private val description: TextView = itemView.findViewById(R.id.description)

    fun bind(film: Film) {
        title.text = film.title
        //создаем для каждого постера элемента свой transitionName
        poster.transitionName = "poster_${film.title}"
        Glide.with(itemView)
            .load(film.poster)
            .centerCrop()
            .into(poster)
        description.text = film.description

    }
}

class FilmListAdapter(private val clickListener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //текущий список фильмов, который отображается в RecyclerView
    private var items = mutableListOf<Film>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false)
        return FilmViewHolder(view)
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

class FilmDiffUtil(val oldList: List<Film>, val newList: List<Film>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].poster == newList[newItemPosition].poster &&
                oldList[oldItemPosition].description == newList[newItemPosition].description
    }
}

class FilmListItemDecor(private val paddingInDp: Int) : RecyclerView.ItemDecoration() {
    private val Int.convertPx: Int
        get() {
            return this * Resources.getSystem().displayMetrics.density.toInt()
        }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = paddingInDp.convertPx
        outRect.right = paddingInDp.convertPx
        outRect.left = paddingInDp.convertPx
    }
}


@Parcelize
data class Film(
    val title: String,
    val poster: Int,
    val description: String,
    var isInFavorites: Boolean = false,
) : Parcelable


