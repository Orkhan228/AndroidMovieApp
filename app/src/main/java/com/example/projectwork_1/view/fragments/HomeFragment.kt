package com.example.projectwork_1.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.databinding.FragmentHomeBinding
import com.example.projectwork_1.utils.AnimationHelper
import com.example.projectwork_1.view.activities.MainActivity
import com.example.projectwork_1.view.rv_adapters.FilmListItemDecor
import com.example.projectwork_1.view.rv_adapters.FilmListAdapter
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.getValue


class HomeFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var rootView: CoordinatorLayout
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: FilmListAdapter
    private lateinit var sharedPref: SharedPreferences
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private val viewModel: SharedFilmsViewModel by activityViewModels()
    private var filmsDataBase = mutableListOf<Film>()
        set(value) {
            if (field == value) return
            field = value
            adapter.addItems(field)
        }
    private val compDisposable = CompositeDisposable()

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

        adapter = FilmListAdapter(object : FilmListAdapter.OnItemClickListener {
            //При клике мы открываем фрагмент с деталями, передаем туда фильм на который мы нажали, и изображение
            override fun click(film: Film, posterView: ImageView) {
                (requireActivity() as MainActivity).launchDetFragment(film, posterView)
            }
        })

        compDisposable.add(
            viewModel.filmsListFlowableData
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { films ->
                        filmsDataBase = films.toMutableList()
                        adapter.addItems(filmsDataBase)
                    },
                    { e ->
                        println("!!! HomeFragmentProblem $e")
                    }
                ))

        compDisposable.add(
            viewModel.showProgressBarFlow
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.progressBar.isVisible = it
                })
        )


        viewModel.showErrorData.observe(viewLifecycleOwner, Observer<String> {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireActivity())
        val decorator = FilmListItemDecor(8)
        recyclerView?.addItemDecoration(decorator)

        //инициализируем обновление экрана
        initPullRefresh()

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
//                    viewModel.loadNextPage()
                    compDisposable.add(
                    viewModel.searchNextFilm()
                        .subscribe(
                            {
                                adapter.addItemsPagination(it as MutableList<Film>)
                            },
                            { e -> println("!!! Problem with search ${e.printStackTrace()}") }
                        )
                    )
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
                    compDisposable.add(
                    viewModel.searchFilm()
                        .subscribe(
                            {
                                adapter.addItems(it as MutableList<Film>)

                            },
                            { e -> println("!!! Problem with search ${e.printStackTrace()}") }
                        )
                    )
                    viewModel.searchSubject.onNext(newText)
                    return true
                }
            }
        })
        AnimationHelper.performFragmentCircularRevealAnimation(rootView, requireActivity(), 1)

        //Задание со звездочкой
        listener = object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(
                sharedPreferences: SharedPreferences?,
                key: String?,
            ) {
                when (key) {
                    KEY_DEFAULT_CATEGORY -> refreshHomeFragment()
                }
            }
        }

        sharedPref = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        //регаем наш слушатель
        sharedPref.registerOnSharedPreferenceChangeListener(listener)
    }


    //В этом методе убираем слушатель, чтобы он не занимал память просто так
    override fun onPause() {
        super.onPause()

        sharedPref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    //Обычное задание
    private fun initPullRefresh() {
        binding.pullToRefresh.setOnRefreshListener {
            refreshHomeFragment()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    //В этом методе мы очищаем
    private fun refreshHomeFragment() {
        viewModel.filmsLogic()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compDisposable.clear()
    }

    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
    }
}

