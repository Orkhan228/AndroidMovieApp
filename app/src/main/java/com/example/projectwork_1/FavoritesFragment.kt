package com.example.projectwork_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FavoritesFragment : Fragment() {

    private val favoritesDataBase = FilmsDatabase.favoriteFilms

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

        val favoritesRecycler = view.findViewById<RecyclerView>(R.id.favorites_recycler_view)

        var favoritesList = emptyList<Film>()
        favoritesList = favoritesDataBase

        val adapter = HomeFragment.FilmListAdapter(object : HomeFragment.FilmListAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchDetFragment(film)
            }
        })

        adapter.addItems(favoritesList)

        favoritesRecycler.adapter = adapter
        favoritesRecycler.layoutManager = LinearLayoutManager(requireContext())
        favoritesRecycler.addItemDecoration(HomeFragment.FilmListItemDecor(8))


    }
}