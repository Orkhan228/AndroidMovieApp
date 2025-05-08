package com.example.projectwork_1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    }

    //Решил обойтись без синтетиков, так как они устарели.
    fun menuToast(view: View) {
        Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show()
    }

    fun favToast(view: View) {
        Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show()
    }

    fun watLatToast(view: View) {
        Toast.makeText(this, "Watch Later", Toast.LENGTH_SHORT).show()
    }

    fun colToast(view: View) {
        Toast.makeText(this, "Collections", Toast.LENGTH_SHORT).show()
    }

    fun setToast(view: View) {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
    }

}