package com.example.projectwork_1

import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

lateinit var mainLayout: ConstraintLayout
private var lastFragmentTag: String? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //Проверяем текущую версию API смартфона, если больше или равно 30, то говорим системе не настраивать отступы,
        //мы сами их сделаем
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        //Если меньше 30, то система сама настраивает отступы
        else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_menu)

        //WindowInsetsControllerCompat - Это обёртка (класс совместимости из AndroidX), которая управляет системными окнами (status bar, navigation bar, жестовые панели и т.д.)
        //window → текущее окно активности (MainActivity), в котором рисуется интерфейс
        //window.decorView → корневое View окна (над всеми твоими layout’ами). Через него можно управлять поведением системных элементов.
        val wic = WindowInsetsControllerCompat(window, window.decorView)
        //С помощью объекта-контроллера меняем цвет статус бара на белый.
        wic.isAppearanceLightStatusBars = false  // false → светлый текст, true → тёмный текст

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
                val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        systemBarsInsets.bottom
                    }
                    else {
                        0
                    }
                }
                insets
            }
        } else {
            // Для API <30 используем обычные отступы
            bottomNavigationView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = 0
            }
        }

        mainLayout = findViewById<ConstraintLayout>(R.id.main)

        initNavigation()
        //animStart()
        //objAnimStart()
        startFragment()
    }

    private fun checkFragmentExistence(tag: String): Fragment? {
        val tagFragment = supportFragmentManager.findFragmentByTag(tag)
        return tagFragment
    }

    private fun changeFragment(fragment: Fragment, tag: String) {
        lastFragmentTag?.let { prevTag ->
            //Здесь я использую constantState?.newDrawable()?.mutate(), чтобы сделать копию drawable.
            //Иначе фон просто «переедет» из фрагмента (и у него он исчезнет).
            val prevFragment = supportFragmentManager.findFragmentByTag(prevTag)
            val bg = prevFragment?.view?.background
            mainLayout.background = bg?.constantState?.newDrawable()?.mutate()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .addToBackStack(null)
            .commit()

        lastFragmentTag = tag
    }

    override fun onBackPressed() {

        //Переопределил этот метод, для того чтобы кнопка системная назад,
        //работала только для фрагмента с деталями и выхода из приложения.
        val fm = supportFragmentManager
        val fragments = fm.fragments
        val currentFragment = fragments.lastOrNull { it.isVisible }

        when (currentFragment) {
            is DetailsFragment -> fm.popBackStack()
            is HomeFragment -> {
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.baseline_exit_to_app_24)
                    .setTitle("Вы действительно хотите выйти?")
                    .setNegativeButton("Нет") { _, _ ->

                    }
                    .setPositiveButton("Да") { _, _ ->
                        super.onBackPressed()
                        finish()
                    }
                    .show()
            }

            else -> {
            }
        }
    }

    fun startFragment() {
        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragment_container, HomeFragment(), "mainMenu")
            .commit()
    }

    //Для начала изменил сигнатуру метода, добавив изображение, которое и является общим элементом
    fun launchDetFragment(film: Film, posterView: ImageView) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)

        val secondFragment = DetailsFragment()
        secondFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            //добавляем общий элемент, из сигнатуры метода
            .addSharedElement(posterView, posterView.transitionName)
            .replace(R.id.fragment_container, secondFragment, "details")
            //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)
            .commit()
    }

    fun initNavigation() {

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottom_view = findViewById<BottomNavigationView>(R.id.bottom_menu)

        topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

        topAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "There will be navigation here someday", Toast.LENGTH_SHORT).show()
        }

        bottom_view.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    val tag = "favorites"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: FavoritesFragment(), tag)


                    true
                }

                R.id.watch_later -> {
                    val tag = "watchLater"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: WatchLaterFragment(), tag)

                    true
                }

                R.id.collections -> {
                    val tag = "collections"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: CollectionsFragment(), tag)


                    true
                }

                R.id.main_menu -> {
                    val tag = "mainMenu"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: HomeFragment(), tag)

                    true
                }

                else -> false
            }
        }
        bottom_view.selectedItemId = R.id.main_menu

    }
}


//Для теста работы DiffUtil
//        val diffTest = mutableListOf<Film>()
//        diffTest.addAll(filmDataBase)
//        diffTest.add(Film("Hulk", R.drawable.incredible_hulk, "Он большой. Он сильный. Он вспыльчив. И своей славой он обязан сомнительным веществам, повышающим работоспособность." +
//                " Нет, нет, я не говорю об определенном бейсболисте " +
//                "высшей лиги. Я имею в виду Халка - зеленое чудовище ростом 9 футов, которое рычит \"Hulk smash!\"" +
//                " и воспринимает это как обещание."))
//        val bottomMenu = findViewById<BottomNavigationView>(R.id.bottom_menu)
//        bottomMenu.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.collections -> adapter.addItems(diffTest)
//            }
//            false
//        }
//Вынес код анимаций в отдельный метод. ViewPropertyAnimation это как второй способ реализации анимаций.
//    fun animStart() {
//        val poster1Anim = findViewById<CardView>(R.id.poster_1)
//        val poster2Anim = findViewById<CardView>(R.id.poster_2)
//        val poster3Anim = findViewById<CardView>(R.id.poster_3)
//        val poster4Anim = findViewById<CardView>(R.id.poster_4)
//
//        poster1Anim.scaleX = 0f
//        poster1Anim.scaleY = 0f
//        poster1Anim.alpha = 0f
//
//        poster2Anim.scaleX = 0f
//        poster2Anim.scaleY = 0f
//        poster2Anim.alpha = 0f
//
//        poster3Anim.scaleX = 0f
//        poster3Anim.scaleY = 0f
//        poster3Anim.alpha = 0f
//
//        poster4Anim.scaleX = 0f
//        poster4Anim.scaleY = 0f
//        poster4Anim.alpha = 0f
//
//        poster1Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//
//        poster2Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//
//        poster3Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//
//        poster4Anim.animate()
//            .setDuration(650)
//            .scaleX(1f)
//            .scaleY(1f)
//            .alpha(1f)
//            .start()
//    }

//Третий способ через ObjectAnimator/ValueAnimator
//    fun objAnimStart() {
//
//        val poster1Anim = findViewById<CardView>(R.id.poster_1)
//        val poster2Anim = findViewById<CardView>(R.id.poster_2)
//        val poster3Anim = findViewById<CardView>(R.id.poster_3)
//        val poster4Anim = findViewById<CardView>(R.id.poster_4)
//
//        val anim1X = ObjectAnimator.ofFloat(poster1Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim1Y = ObjectAnimator.ofFloat(poster1Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim1A = ObjectAnimator.ofFloat(poster1Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val anim2X = ObjectAnimator.ofFloat(poster2Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim2Y = ObjectAnimator.ofFloat(poster2Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim2A = ObjectAnimator.ofFloat(poster2Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val anim3X = ObjectAnimator.ofFloat(poster3Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim3Y = ObjectAnimator.ofFloat(poster3Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim3A = ObjectAnimator.ofFloat(poster3Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val anim4X = ObjectAnimator.ofFloat(poster4Anim, View.SCALE_X, 0f, 1f).setDuration(650)
//        val anim4Y = ObjectAnimator.ofFloat(poster4Anim, View.SCALE_Y, 0f, 1f).setDuration(650)
//        val anim4A = ObjectAnimator.ofFloat(poster4Anim, View.ALPHA, 0f, 1f).setDuration(650)
//
//        val animatorSet = AnimatorSet ()
//        animatorSet.playTogether(anim1X, anim1Y, anim1A, anim2X, anim2Y, anim2A, anim3X, anim3Y, anim3A, anim4X, anim4Y, anim4A)
//        animatorSet.start()
//    }

//module24 Создание RecyclerView

//private lateinit var filmsAdapter: FilmListAdapter



