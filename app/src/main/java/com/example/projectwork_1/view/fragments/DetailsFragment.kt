package com.example.projectwork_1.view.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.projectwork_1.utils.ApiConstants
import com.example.projectwork_1.R
import com.example.projectwork_1.databinding.FragmentDetailsBinding
import com.example.projectwork_1.domain.Film
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform

class DetailsFragment : Fragment() {

    private lateinit var detDesc: TextView
    private lateinit var detPost: AppCompatImageView
    private lateinit var detToolBar: Toolbar
    private lateinit var detFabShare: FloatingActionButton
    private lateinit var coordinatorLay: CoordinatorLayout
    private lateinit var detFabFav: FloatingActionButton
    private lateinit var binding: FragmentDetailsBinding
    private var favDataBase = mutableListOf<Film>()
        set(value) {
            if (field == value) return
            field = value
        }

    private val viewModel: SharedFilmsViewModel by activityViewModels()

    init {

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            //это тот вьюгруп, где имеются два фрагмента, через которых и будет проходить анимация с общим элементом
            drawingViewId = R.id.fragment_container
            duration = 500
            //цвет фона
            scrimColor = Color.TRANSPARENT
            propagation = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favFilmsLiveData.observe(viewLifecycleOwner, Observer<List<Film>> {
            favDataBase = it.toMutableList()
        })

        postponeEnterTransition()

        detDesc = binding.detailsDescription
        detPost = binding.detailsPoster
        detToolBar = binding.detailsToolbar
        detFabShare = binding.detailsFab
        coordinatorLay = binding.coordinatorLay
        detFabFav = binding.detailsFabFav

        detActivity()
        startPostponedEnterTransition()
    }

    fun detActivity() {

        val film = arguments?.getParcelable<Film>("film")
        val filmTitle = film?.title

        if (film == null) {
            detPost.setImageResource(R.drawable.baseline_error_24)
            detDesc.text = "There was an error occurred!"
            detToolBar.title = "Error occurred!"
        } else {
            Glide.with(this)
                .load(ApiConstants.IMAGES_URL + "w780" + film.poster)
                .centerCrop()
                .into(detPost)
            detDesc.text = film.description
            detToolBar.title = film.title
            //делаем транзишнНейм одинаковым
            detPost.transitionName = "poster_$filmTitle"
        }

        detFabShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Глянь этот фильм: ${film?.title} \n \n ${film?.description}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Поделиться:"))
        }

        detFabFav.setImageResource(
            if (film!!.isInFavorites) R.drawable.baseline_favorite_24
            else R.drawable.baseline_favorite_border_24
        )

        detFabFav.setOnClickListener {

            if (!film.isInFavorites) {
                film.isInFavorites = true
                viewModel.addToFavorites(film)
                detFabFav.setImageResource(R.drawable.baseline_favorite_24)
                Toast.makeText(requireContext(), "Добавлено в Избранное", Toast.LENGTH_SHORT).show()
            } else {
                film.isInFavorites = false
                viewModel.removeFromFavorites(film)
                detFabFav.setImageResource(R.drawable.baseline_favorite_border_24)
                Toast.makeText(requireContext(), "Удалено в Избранное", Toast.LENGTH_SHORT).show()
            }
        }
    }
}