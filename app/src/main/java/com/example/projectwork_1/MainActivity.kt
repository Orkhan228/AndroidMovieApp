package com.example.projectwork_1

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initNavigation()
        //animStart()
        //objAnimStart()
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

        bottom_view.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.favorites -> {
                    Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.watch_later -> {
                    Toast.makeText(this, "Watch later", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.collections -> {
                    Toast.makeText(this, "Collections", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }

    }
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


}