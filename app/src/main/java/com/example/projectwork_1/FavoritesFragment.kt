package com.example.projectwork_1

import android.os.Bundle
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionSet
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialFade

lateinit var rootViewFav: FrameLayout

class FavoritesFragment : Fragment() {

    private val favoritesDataBase = FilmsDatabase.favoriteFilms

//    init {
//        enterTransition = MaterialFade().apply {
//            duration = 600
//            mode = MaterialFade.MODE_IN
//            propagation = null
//        }
//
//        returnTransition = MaterialFade().apply {
//            duration = 600
//            mode = MaterialFade.MODE_OUT
//            propagation = null
//        }
//
//        exitTransition = MaterialFade().apply {
//            duration = 515
//            propagation = null
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootViewFav = view.findViewById<FrameLayout>(R.id.fav_root)

        val favoritesRecycler = view.findViewById<RecyclerView>(R.id.favorites_recycler_view)

        var favoritesList = emptyList<Film>()
        favoritesList = favoritesDataBase

        val adapter =
            FilmListAdapter(object : FilmListAdapter.OnItemClickListener {
                override fun click(film: Film, posterView: ImageView) {
                    (requireActivity() as MainActivity).launchDetFragment(film, posterView)
                }
            })

        adapter.addItems(favoritesList)

        favoritesRecycler.adapter = adapter
        favoritesRecycler.layoutManager = LinearLayoutManager(requireContext())
        favoritesRecycler.addItemDecoration(FilmListItemDecor(8))

        AnimationHelper.performFragmentCircularRevealAnimation(rootViewFav, requireActivity(), 2)
    }
}