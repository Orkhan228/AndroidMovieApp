package com.example.projectwork_1.view.activities

import android.app.ComponentCaller
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.App
import com.example.projectwork_1.R
import com.example.projectwork_1.databinding.ActivityMainBinding
import com.example.projectwork_1.utils.NotificationConstants
import com.example.projectwork_1.view.fragments.CollectionsFragment
import com.example.projectwork_1.view.fragments.DetailsFragment
import com.example.projectwork_1.view.fragments.FavoritesFragment
import com.example.projectwork_1.view.fragments.HomeFragment
import com.example.projectwork_1.view.fragments.SettingsFragment
import com.example.projectwork_1.view.fragments.WatchLaterFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


private lateinit var mainLayout: ConstraintLayout
private var lastFragmentTag: String? = null
private lateinit var bottomNavigationView: BottomNavigationView
private lateinit var notificationManager: NotificationManager
private lateinit var notificationChannel: NotificationChannel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = binding.bottomMenu

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

        mainLayout = binding.main

        notificationManager = App.instance.notificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createNotfChannel())
        }

        initNavigation()
        startFragment()
        handleNotificationIntent(intent)
        binding.titleToolBar = "Search It!"

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
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
                        super.onBackPressedDispatcher.onBackPressed()
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
            .addToBackStack(null)
            .commit()
    }

    fun initNavigation() {

        val topAppBar = binding.topAppBar

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

        bottomNavigationView.setOnItemSelectedListener {

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

                R.id.settings -> {
                    val tag = "settings"
                    val fragment = checkFragmentExistence(tag)
                    changeFragment(fragment ?: SettingsFragment(), tag)

                    true
                }

                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.main_menu
    }

    fun createNotfChannel(): NotificationChannel {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                NotificationConstants.CHANNEL_ID,
                NotificationConstants.CHANNEL_NAME,
                NotificationConstants.CHANNEL_IMPORTANCE
            ).apply {
                description = NotificationConstants.CHANNEL_DESCRIPTION
            }
        }
        return notificationChannel
    }

    private fun handleNotificationIntent(intent: Intent) {
        val film = intent.getParcelableExtra<Film>(NotificationConstants.EXTRA_FILM_ID)

        if (film == null) return

        openFilmDetails(film)
    }

    private fun openFilmDetails(film: Film) {
        val bundle = Bundle()
        bundle.putParcelable("film", film)

        val detailsFragment = DetailsFragment()
        detailsFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, detailsFragment)
            .addToBackStack(null)
            .commit()
    }
}