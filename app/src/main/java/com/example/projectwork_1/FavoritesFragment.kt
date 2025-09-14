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
import com.example.projectwork_1.databinding.ActivityMainBinding
import com.example.projectwork_1.databinding.FragmentFavoritesBinding
import com.google.android.material.transition.MaterialFade



class FavoritesFragment : Fragment() {

    private lateinit var rootViewFav: FrameLayout
    private val favoritesDataBase = FilmsDatabase.favoriteFilms
    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootViewFav = binding.favRoot

        val favoritesRecycler = binding.favoritesRecyclerView

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