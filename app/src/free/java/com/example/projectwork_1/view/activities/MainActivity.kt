package com.example.projectwork_1.view.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
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
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.getValue
import androidx.activity.viewModels
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import androidx.core.content.edit


private lateinit var mainLayout: ConstraintLayout
private var lastFragmentTag: String? = null
private lateinit var bottomNavigationView: BottomNavigationView
private lateinit var notificationManager: NotificationManager
private lateinit var notificationChannel: NotificationChannel
private lateinit var compositeDisposable: CompositeDisposable



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var trialExpired = false

    private val viewModel: SharedFilmsViewModel by viewModels()

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

        compositeDisposable = CompositeDisposable()

        if (isTrialExpired(this)) {
            trialExpired = true
            showTrialExpiredDialog(this)

        }
        else {
            trialExpired = false
            showTrialDialog(this)
        }

        initNavigation()
        startFragment()
        handleNotificationIntent(intent)
        binding.titleToolBar = "Search It!"

        val prefs = getSharedPreferences(SHARED_PREF_TRIAL_NAME, MODE_PRIVATE)
        if (!prefs.contains(SHARED_PREF_FIRST_LAUNCH)) {
            prefs.edit {
                putLong("firstLaunch", System.currentTimeMillis())
            }
        }
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
                    //так как версия беслпатная, то выводим тост об этом и не открываем этот экран.
                    Toast.makeText(this@MainActivity, "Данный экран не доступен, из-за бесплатной версии!", Toast.LENGTH_SHORT).show()


                    true
                }

                R.id.watch_later -> {
                    if (trialExpired) {
                        Toast.makeText(this@MainActivity, "Пробный период истёк. Данный экран больше недоступен!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val tag = "watchLater"
                        val fragment = checkFragmentExistence(tag)
                        changeFragment(fragment ?: WatchLaterFragment(), tag)
                    }


                    true
                }

                R.id.collections -> {
                    //так как версия беслпатная, то выводим тост об этом и не открываем этот экран.
                    Toast.makeText(this@MainActivity, "Данный экран не доступен, из-за бесплатной версии!", Toast.LENGTH_SHORT).show()


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
        val filmID = intent.getIntExtra(NotificationConstants.EXTRA_FILM_ID, -1)
        if (filmID == -1) return

        compositeDisposable.add(
            viewModel.filmsListFlowableData
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { films ->
                        films.find { it.id == filmID }?.let { film -> openFilmDetails(film) }
                    },
                    { e ->
                        Log.e("MainActivity", "Notification handling error", e)
                    })
        )

    }

    private fun isTrialExpired(context: Context): Boolean {
        val prefs = context.getSharedPreferences(SHARED_PREF_TRIAL_NAME, Context.MODE_PRIVATE)
        val firstStart = prefs.getLong(SHARED_PREF_FIRST_LAUNCH, 0L)

        val trialDays = 7L
        val trialDaysInMs = trialDays * 24 * 60 * 60 * 1000

        val result = System.currentTimeMillis() - firstStart > trialDaysInMs

        val prefsBoolean = context.getSharedPreferences(SHARED_PREF_IS_TRIAL_EXPIRED_NAME, Context.MODE_PRIVATE)
        prefsBoolean.edit {
            putBoolean(SHARED_PREF_IS_TRIAL_EXPIRED, result)
        }

        return result
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

    private fun showTrialDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Пробная версия")
            .setMessage("У вас активен пробный период на 7 дней. После его окончания некоторые функции(такие как, \"Посмотреть позже\") будут заблокированы.")
            .setPositiveButton("Ок", null)
            .show()
    }

    private fun showTrialExpiredDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Пробная версия окончилась")
            .setMessage("У вас больше не активен пробный период на 7 дней. Такие функции как, \"Посмотреть позже\"  заблокированы.")
            .setPositiveButton("Ок", null)
            .show()
    }

    companion object {
        const val SHARED_PREF_TRIAL_NAME = "trial"
        const val SHARED_PREF_FIRST_LAUNCH = "firstLaunch"
        const val SHARED_PREF_IS_TRIAL_EXPIRED = "SHARED_PREF_IS_TRIAL_EXPIRED"
        const val SHARED_PREF_IS_TRIAL_EXPIRED_NAME = "SHARED_PREF_IS_TRIAL_EXPIRED_NAME"
    }
}