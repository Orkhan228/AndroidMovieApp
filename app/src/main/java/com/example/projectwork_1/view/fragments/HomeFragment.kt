package com.example.projectwork_1.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectwork_1.view.activities.MainActivity
import com.example.projectwork_1.databinding.FragmentHomeBinding
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.utils.AnimationHelper
import com.example.projectwork_1.view.rv_adapters.FilmListItemDecor
import com.example.projectwork_1.view.rv_adapters.FilmListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var rootView: CoordinatorLayout
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: FilmListAdapter
    private val viewModel: SharedFilmsViewModel by activityViewModels()
    private var filmsDataBase = mutableListOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            adapter.addItems(field)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        searchView = binding.searchView
        rootView = binding.homeFragmentRoot

        viewModel.filmsListLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            filmsDataBase = it.toMutableList()
        })

        adapter = FilmListAdapter(object : FilmListAdapter.OnItemClickListener {
            //При клике мы открываем фрагмент с деталями, передаем туда фильм на который мы нажали, и изображение
            override fun click(film: Film, posterView: ImageView) {
                (requireActivity() as MainActivity).launchDetFragment(film, posterView)
            }
        })
        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val decorator = FilmListItemDecor(8)
        recyclerView?.addItemDecoration(decorator)

        adapter.addItems(filmsDataBase)

        //добавляем слушатель на скролл ресайлер вью
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int,
            ) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                //делаем логику, того что если осталось пять последних айтемов, то начинаем загрузку
                if (totalItemCount <= lastVisibleItem + 5) {
                    viewModel.loadNextPage()
                }
            }
        })

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
                    adapter.addItems(filmsDataBase)
                    return true
                } else {
                    val result = filmsDataBase.filter {
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

