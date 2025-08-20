package com.example.projectwork_1

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.Slide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class DetailsFragment : Fragment() {

    lateinit var detDesc: TextView
    lateinit var detPost: AppCompatImageView
    lateinit var detToolBar: Toolbar
    lateinit var detFabShare: FloatingActionButton
    lateinit var coordinatorLay: CoordinatorLayout
    lateinit var botNav: BottomNavigationView
    lateinit var detFabFav: FloatingActionButton
    private val favDataBase = FilmsDatabase.favoriteFilms

    init {
        enterTransition = Slide(Gravity.END).apply {
            duration = 1050
            interpolator = DecelerateInterpolator()
            propagation = null
        }

        returnTransition = Fade(Fade.MODE_OUT).apply {
            duration = 800
            propagation = null
        }

        exitTransition = Fade(Fade.MODE_OUT).apply {
            duration = 800
            propagation = null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        detDesc = view.findViewById<TextView>(R.id.details_description)
        detPost = view.findViewById<AppCompatImageView>(R.id.details_poster)
        detToolBar = view.findViewById<Toolbar>(R.id.details_toolbar)
        detFabShare = view.findViewById<FloatingActionButton>(R.id.details_fab)
        coordinatorLay = view.findViewById<CoordinatorLayout>(R.id.coordinator_lay)
        //botNav = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        detFabFav = view.findViewById<FloatingActionButton>(R.id.details_fab_fav)


        detActivity()
        startPostponedEnterTransition()
    }

    fun detActivity() {

        val film = arguments?.getParcelable<Film>("film")
        if (film == null) {
            detPost.setImageResource(R.drawable.baseline_error_24)
            detDesc.text = "There was an error occurred!"
            detToolBar.title = "Error occurred!"
        } else {
            detPost.setImageResource(film.poster)
            detDesc.text = film.description
            detToolBar.title = film.title
        }

//        botNav.setOnItemSelectedListener {
//            when(it.itemId) {
//                R.id.bot_fav -> Snackbar.make(coordinatorLay, "Добавлено в \"избранное\"", Snackbar.LENGTH_SHORT).setAction("Удалить") {
//                    Toast.makeText(requireActivity(), "Удалено", Toast.LENGTH_SHORT).show()
//                }.setActionTextColor(ContextCompat.getColor(requireActivity(), R.color.colorLightPrimary)).show()
//                R.id.bot_watch_later -> Snackbar.make(coordinatorLay, "Добавлено в \"Смотреть позже\"", Snackbar.LENGTH_SHORT).setAction("Удалить") {
//                    Toast.makeText(requireActivity(), "Удалено", Toast.LENGTH_SHORT).show()
//                }.setActionTextColor(ContextCompat.getColor(requireActivity(), R.color.colorLightPrimary)).show()
//            }
//            false
//        }

        detFabShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, "Глянь этот фильм: ${film?.title} \n \n ${film?.description}")
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
                favDataBase.add(film)
                detFabFav.setImageResource(R.drawable.baseline_favorite_24)
                Toast.makeText(requireContext(), "Добавлено в Избранное", Toast.LENGTH_SHORT).show()

            }
            else {
                film.isInFavorites = false
                favDataBase.remove(film)
                detFabFav.setImageResource(R.drawable.baseline_favorite_border_24)
                Toast.makeText(requireContext(), "Удалено в Избранное", Toast.LENGTH_SHORT).show()

            }

        }
    }
}