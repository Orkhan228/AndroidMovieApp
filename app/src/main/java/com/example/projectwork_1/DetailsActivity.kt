package com.example.projectwork_1

import android.os.Bundle
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
import com.example.projectwork_1.MainActivity.Film
import com.example.projectwork_1.R.string.DescriptionError
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinator_lay)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        detActivity()
    }

    fun detActivity() {
        val detDesc = findViewById<TextView>(R.id.details_description)
        val detPost = findViewById<AppCompatImageView>(R.id.details_poster)
        val detToolBar = findViewById<Toolbar>(R.id.details_toolbar)
        val detFab = findViewById<FloatingActionButton>(R.id.details_fab)
        val coordinatorLay = findViewById<CoordinatorLayout>(R.id.coordinator_lay)
        val botNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val film = intent.extras?.get("film") as Film
        if (film == null) {
            detPost.setImageResource(R.drawable.baseline_error_24)
            detDesc.text = "There was an error occurred!"
            detToolBar.title = "Error occurred!"
        }
        else {
            detPost.setImageResource(film.poster)
            detDesc.text = film.description
            detToolBar.title = film.title
        }

        botNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bot_fav -> Snackbar.make(coordinatorLay, "Добавлено в \"избранное\"", Snackbar.LENGTH_SHORT).setAction("Удалить") {
                    Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
                }.setActionTextColor(ContextCompat.getColor(this, R.color.colorLightPrimary)).show()
                R.id.bot_watch_later -> Snackbar.make(coordinatorLay, "Добавлено в \"Смотреть позже\"", Snackbar.LENGTH_SHORT).setAction("Удалить") {
                    Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
                }.setActionTextColor(ContextCompat.getColor(this, R.color.colorLightPrimary)).show()
            }
            false
        }

        detFab.setOnClickListener {
            Toast.makeText(this, "Вы поделились этим фильмом", Toast.LENGTH_SHORT).show()
        }

    }
}