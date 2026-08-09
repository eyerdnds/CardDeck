package com.example.murycs

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val vistaPrincipal = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(vistaPrincipal) { v, insets ->
            val barrasSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barrasSistema.left, barrasSistema.top, barrasSistema.right, 0) // Bottom padding handled by BottomNav
            insets
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navigation_favorites -> {
                    replaceFragment(FavoritesFragment())
                    true
                }
                else -> false
            }
        }

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}