package com.example.projectwork_1.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectwork_1.view.activities.MainActivity
import com.example.projectwork_1.databinding.FragmentFavoritesBinding
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.utils.AnimationHelper
import com.example.projectwork_1.view.rv_adapters.FilmListItemDecor
import com.example.projectwork_1.view.rv_adapters.FilmListAdapter
import com.example.projectwork_1.viewmodel.FavoritesFragmentViewModel
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var rootViewFav: FrameLayout
    private lateinit var adapter: FilmListAdapter
    private val viewModel: SharedFilmsViewModel by activityViewModels()
    private var favFilmsDataBase = mutableListOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            adapter.addItems(field)
        }

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

        viewModel.favFilmsLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            favFilmsDataBase = it.toMutableList()
        })

        rootViewFav = binding.favRoot

        val favoritesRecycler = binding.favoritesRecyclerView

        adapter =
            FilmListAdapter(object : FilmListAdapter.OnItemClickListener {
                override fun click(film: Film, posterView: ImageView) {
                    (requireActivity() as MainActivity).launchDetFragment(film, posterView)
                }
            })

        adapter.addItems(favFilmsDataBase)

        favoritesRecycler.adapter = adapter
        favoritesRecycler.layoutManager = LinearLayoutManager(requireContext())
        favoritesRecycler.addItemDecoration(FilmListItemDecor(8))

        AnimationHelper.performFragmentCircularRevealAnimation(rootViewFav, requireActivity(), 2)
    }
}